#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use Digest::MD5 qw(md5_hex);
use File::Basename;
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
	my $jobstring  = "$povray -I$jobref->{filename} -W$jobref->{outputwidth} -H$jobref->{outputheigth} ";
	my $shortfn = basename($jobref->{filename});
	my $dirname = "$outputdir" . "/" . $shortfn;
	   $jobstring .= "-SR$jobref->{startrow} -SC$jobref->{startcol} -ER$jobref->{endrow} ";
	   $jobstring .= "-EC$jobref->{endcol} -O$dirname/";
	
	mkdir "$dirname/$jobhash";
	$jobstring .= $jobhash . "/output.tga";
	$jobref->{workingdir} = "./$dirname/$jobhash/";
	open(FH,">$jobref->{workingdir}run.sh") || die "Could not open output file $jobref->{workingdir}run.sh\n"; 
	print FH ("#!/bin/sh\n$jobstring\n");	
	close(FH);
	$jobref->{startscript} = "$jobref->{workingdir}run.sh";
}


sub submit
{
	my $job = shift;
	my $result = "";
		
	unless($dry)
	{
		$result = qx(qsub -w $job->{workingdir} $job->{startscript});
		chomp($result);
	}
	return $result;
}

sub main
{
	my $xstep = 0;
	my $ystep = 0;
	my $xrest = 0;
	my $yrest = 0;
	my @pics = ();

	foreach my $if (@inputfiles)
	{
		my %pic = ( filename => $if, jobs => {}); 
		my $dirname = "$outputdir" . "/" . basename($if);
		mkdir $dirname;
		push(@pics,\%pic);
	}
	
	my $cpus = &getCPUNumber();
	if ($maxnodes != 0 and not($maxnodes > $cpus)) {
		$cpus = $maxnodes;
	}
	
	print("Computing with $cpus nodes\n");

	$xstep = int($width/$cpus);
	$xrest = $width % $cpus;
	$ystep = int($heigth/$cpus);
	$yrest = $heigth % $cpus;


	foreach my $pic (@pics)
	{

		if($xrest == 0)
		{
			for ( my $x = 0; $x < $width; $x+= $xstep )
			{
				my $jobhash = md5_hex("$pic->{filename},$x");
				$pic->{jobs}->{$jobhash}->{filename}     = $pic->{filename};
				$pic->{jobs}->{$jobhash}->{startrow}     = 1;
				$pic->{jobs}->{$jobhash}->{endrow}       = $heigth;
				$pic->{jobs}->{$jobhash}->{startcol}     = $x+1;
				$pic->{jobs}->{$jobhash}->{endcol}       = $x+$xstep;
				$pic->{jobs}->{$jobhash}->{outputwidth}  = $width;
				$pic->{jobs}->{$jobhash}->{outputheigth} = $heigth;
				&createjob($pic->{jobs}->{$jobhash},$jobhash);
			} 

		}
		else
		{
			for ( my $y = 0; $y < $heigth; $y+= $ystep )
			{	
				my $offset = 0;
				if($yrest != 0)
				{	
					$offset=1;
					$yrest--;
				}
				my $jobhash = md5_hex("$pic->{filename},$y");
				$pic->{jobs}->{$jobhash}->{filename}     = $pic->{filename};
				$pic->{jobs}->{$jobhash}->{startrow}     = $y+1;
				$pic->{jobs}->{$jobhash}->{endrow}       = $y+$ystep+$offset;
				$pic->{jobs}->{$jobhash}->{startcol}     = 1;
				$pic->{jobs}->{$jobhash}->{endcol}       = $width;
				$pic->{jobs}->{$jobhash}->{outputwidth}  = $width;
				$pic->{jobs}->{$jobhash}->{outputheigth} = $heigth;
				&createjob($pic->{jobs}->{$jobhash},$jobhash);
			}
		}

		#actually run the jobs	
		foreach my $job (keys(%{$pic->{jobs}}))
		{
			&submit($pic->{jobs}->{$job});
		}
	}


}

sub getCPUNumber 
{
	my @foo = qx(pbsnodes -q -l free);
	return scalar(@foo);
}
