#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use Digest::MD5 qw(md5_hex);
use File::Basename;
use File::Copy;
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
	--height
	--outputdir
	--maxnodes
	--dry
	--help 	Display this help
EOL



my $result = GetOptions ("width=i" => \$width, 
			"height=i" => \$heigth,
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

&main();





sub createjob
{
	my ($jobref,$jobhash) = @_;
	my $jobstring  = "$povray -I$jobref->{filename} +FT -W$jobref->{outputwidth} -H$jobref->{outputheigth} ";
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

		for ( my $y = 0; $y < $heigth; $y+= $ystep )
		{	
			my $offset = 0;
			if($yrest != 0)
			{	
				$offset=1;
				$yrest--;
			}
			#my $jobhash = md5_hex("$pic->{filename},$y");
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
			&submit($pic->{jobs}->{$job});
		}
	}

	my @mergedpics = ();
	while(scalar(@mergedpics != scalar(@pics)))
	{
		foreach my $pic (@pics)
		{
			my $piccompleted = 1;
			print("=======================================================\n");
			foreach my $job (keys(%{$pic->{jobs}}))
			{
				if( ! -e "$pic->{jobs}->{$job}->{workingdir}/done" )
				{
					print("$job not done yet, retrying\n");
					$piccompleted = 0;
				}
			}
			if($piccompleted)
			{
				&mergepic($pic);
				push(@mergedpics,$pic->{filename});
				print("Merging pic $pic->{filename}\n");
			}
		}
		sleep 5;
	}


}

sub mergepic
{
	my $pic = shift;
	#my $firsthash = md5_hex("$pic->{filename},0");
	
	my $file = basename($pic->{filename},(".pov"));
	chdir($file);
	my @filelist = sort { substr($a,length("$file-")) <=> substr($b,length("$file-")) } (keys(%{$pic->{jobs}}));
	
	copy(shift(@filelist)."/output.tga","../$file.tga");
	foreach my $job (@filelist)
	{
		qx(tail -c +19 $job/output.tga >> ../$file.tga);
	}
}


sub getCPUNumber 
{
	my @foo = qx(pbsnodes -q -l free);
	return scalar(@foo);
}
