#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use File::Basename;
use File::Copy;
use File::Path qw(rmtree);
use Cwd;

my $inputfiles = "";
my $width = 640;
my $heigth = 480;
my $outputdir = ".";
my $maxnodes  = 0;
my $povray = "/usr/bin/povray";
my $help = 0;
my $dry = 0;
my $usage = <<EOL;
USAGE:
	render-parallel [options] inputfile...
	--width
	--heigth
	--outputdir
	--maxnodes
	--dry
	--help 	Display this help
EOL



my $result = GetOptions ("width=i" => \$width, 
			"heigth=i" => \$heigth,
			"ouputdir=s" => \$outputdir,
			"dry"        => \$dry,
			"maxnodes=i" => \$maxnodes,
			"help"       => \$help,
		     );


my @inputfiles = @ARGV;

if ($help or scalar(@inputfiles) == 0)
{
	print($usage);
	exit;
}

foreach (my $i = 0; $i < scalar(@inputfiles); $i++)
{
	if (-e $inputfiles[$i])
	{
		splice(@inputfiles, $i, 1);				
		print("skipping " . $inputfiles[$i] . " as it doesnt exists.\n");
	}
}

&main();





sub createjob
{
	my ($jobref,$jobhash) = @_;
	my $jobstring  = "$povray -I$jobref->{filename} +FT +WL0 -W$jobref->{outputwidth} -H$jobref->{outputheigth} ";
	my $shortfn = basename($jobref->{filename},(".pov"));
	my $currentdir = &Cwd::cwd();
	my $dirname = "$currentdir/$outputdir" . "/" . $shortfn;
	   $jobstring .= "-SR$jobref->{startrow} -SC$jobref->{startcol} -ER$jobref->{endrow} ";
	   $jobstring .= "-EC$jobref->{endcol} +O$dirname/";
	
	mkdir "$dirname/$jobhash";
	$jobstring .= $jobhash . "/output.tga";
	$jobref->{workingdir} = "$dirname/$jobhash/";
	open(FH,">$jobref->{workingdir}run.sh") || die "Could not open output file $jobref->{workingdir}run.sh\n"; 
	print FH ("#!/bin/sh\n$jobstring\necho 42 > $jobref->{workingdir}done");	
	close(FH);
	$jobref->{startscript} = "$jobref->{workingdir}run.sh";
}


sub submit
{
	my $job = shift;
	my $result = "";
		
	unless($dry)
	{
		chdir($job->{workingdir});
		$result = qx(qsub run.sh);
		chomp($result);
		chdir("./../..");
	}
	return $result;
}

sub main
{
	my $ystep = 0;
	my $yrest = 0;
	my @pics = ();
	my %pendingjobs = ();

	foreach my $if (@inputfiles)
	{
		my %pic = ( filename => $if, jobs => {}); 
		my $dirname = "$outputdir" . "/" . basename($if,(".pov"));
		mkdir $dirname;
		push(@pics,\%pic);
	}
	
	my $cpus = &getCPUNumber();
	if ($maxnodes != 0 and not($maxnodes > $cpus)) {
		$cpus = $maxnodes;
	}
	
	print("Computing with $cpus nodes\n");

	$ystep = int($heigth/$cpus);
	$yrest = $heigth % $cpus;


	foreach my $pic (@pics)
	{

		for ( my $y = 0; $y < $heigth-$ystep; $y+= $ystep )
		{	
			my $offset = 0;
			if($yrest != 0)
			{	
				$offset=1;
				$yrest--;
			}
			my $bn = basename $pic->{filename},(".pov");
			my $jobhash = "$bn-$y";
			$pic->{jobs}->{$jobhash}->{filename}     = $pic->{filename};
			$pic->{jobs}->{$jobhash}->{startrow}     = $y+1;
			$pic->{jobs}->{$jobhash}->{endrow}       = $y+$ystep+$offset;
			$pic->{jobs}->{$jobhash}->{startcol}     = 1;
			$pic->{jobs}->{$jobhash}->{endcol}       = $width;
			$pic->{jobs}->{$jobhash}->{outputwidth}  = $width;
			$pic->{jobs}->{$jobhash}->{outputheigth} = $heigth;
			&createjob($pic->{jobs}->{$jobhash},$jobhash);
		}

		#actually run the jobs	
		foreach my $job (keys(%{$pic->{jobs}}))
		{
			my $pbsjobid = &submit($pic->{jobs}->{$job});
			$pic->{jobs}->{$job}->{pbsid} = $pbsjobid;
			$pendingjobs{$job} = $pbsjobid;
			print "submitted $pbsjobid\n";
		}
	}

	my @mergedpics = ();
	my $starttime = time();
	my $threequartertime;
	while(scalar(@mergedpics) != scalar(@pics))
	{
		print("=======================================================\n");
		foreach my $pic (@pics)
		{
			my $piccompleted = 1;
			my $completecounter = 0;
			foreach my $job (keys(%{$pic->{jobs}}))
			{
				if( ! -e "$pic->{jobs}->{$job}->{workingdir}/done" )
				{
					print("$job not done yet, retrying\n");
					$piccompleted = 0;
				}
				else 
				{
					delete $pendingjobs{$job};	
					$completecounter++;
				}
			}
			if($piccompleted)
			{
				&mergepic($pic);
				push(@mergedpics,$pic->{filename});
				print("Merging pic $pic->{filename}\n");
				&cleanup($pic);
			}
			if ($completecounter > int(scalar(keys(%{$pic->{jobs}})) /0.42))
			{
				print "--completed 3/4 of Jobs\n";
				$threequartertime = time();
				if ( ($threequartertime-$starttime)**2 > time()-$starttime)
				{
					foreach my $pendingjob (keys(%pendingjobs))
					{
						$pendingjobs{$pendingjob} = &restartjob($pic,$pendingjob);
						$starttime = time();
						
					}
				}
			}
			if (&quotareached)
			{
				my $userinput = "";
				do
				{
					print "Not enough Disk Space - Holding all Jobs[h] or delete everything[d]?\n";
					$userinput = <STDIN>;
					chomp($userinput);
				} until ($userinput eq "h" or $userinput eq "d");

				if ($userinput eq "d")
				{
					foreach my $pic (keys(%{$pic})) 
					{
						foreach my $job (keys(%{$pic->{jobs}}))
						{
							qx(qdel $pic->{jobs}->{$job}->{pbsid});
						}
						&cleanup($pic);
					}
					exit;
				}
				elsif ($userinput eq "h") 
				{
					foreach my $pendingjob (keys(%pendingjobs)) 
					{
						print "Holding Job $pendingjobs{$pendingjob}\n";
						qx(qhold $pendingjobs{$pendingjob});
					}

					my $userinput = "";
					do
					{
						print "Free Disk Storage, then enter [c]\n";
						$userinput = <STDIN>;
						chomp($userinput);
					} until (not(&quotareaced) and $userinput eq "c");

					foreach my $pendingjob (keys(%pendingjobs)) 
					{
						print "Releasing Job $pendingjobs{$pendingjob}\n";
						qx(qrls $pendingjobs{$pendingjob});
					}
				}
					
			}
		}
		sleep 5;
	}
}

sub quotareached
{
	return 0;
}

sub restartjob
{
	my ($pic,$job) = @_;

	print "restarting job $job";
	qx(qdel $pic->{jobs}->{$job}->{pbsid});
	my $pbsjobid = &submit($pic->{jobs}->{$job});
	$pic->{jobs}->{$job}->{pbsid} = $pbsjobid;
	print "with $pbsjobid\n";
	return $pbsjobid;

}

sub mergepic
{
	my $pic = shift;
	my $file = basename($pic->{filename},(".pov"));
	chdir($file);
	my @filelist = sort { substr($a,length("$file-")) <=> substr($b,length("$file-")) } (keys(%{$pic->{jobs}}));
	
	copy(shift(@filelist)."/output.tga","../$file.tga");
	foreach my $job (@filelist)
	{
		qx(tail -c +19 $job/output.tga >> ../$file.tga);
	}
	chdir("..");
}

sub cleanup
{
	my $pic = shift;
	my $file = basename($pic->{filename},(".pov"));
#	rmtree($file);
	print "rmtree $file\n";
}


sub getCPUNumber 
{
	my @foo = qx(pbsnodes -q -l free);
	return scalar(@foo);
}
