#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use Digest::MD5;
use File::Basename;
my $inputfiles = "";
my $width = 640;
my $heigth = 480;
my $outputdir = ".";
my $maxnodes  = 0;
my $povray = "/usr/bin/povray";
my $help = 0;
my $usage = <<EOL;
USAGE:
	render-parallel [options] inputfile...
	--width
	--height
	--outputdir
	--maxnodes
	--help 	Display this help
EOL



my $result = GetOptions ("width=i" => \$width, 
			"height=i" => \$heigth,
			"ouputdir=s" => \$outputdir,
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
	open(FH,">$dirname/$jobhash/run.sh") || die "Could not open output file\n"; 
	print FH ("#!/bin/sh\n$jobstring\n");	
	close(FH);
	$jobref->{startscript} = "$dirname/$jobhash/run.sh";
}


sub submit
{
	my $job = shift;
	my $result = qx(qsub $job);
	chomp($result);
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
				my $jobhash = md5("$pic->{filename},$x");
				$pic->{jobs}->{$jobhash}->{filename}     = $pic->{filename};
				$pic->{jobs}->{$jobhash}->{startrow}     = $0;
				$pic->{jobs}->{$jobhash}->{endrow}       = $heigth;
				$pic->{jobs}->{$jobhash}->{startcol}     = $x;
				$pic->{jobs}->{$jobhash}->{endcol}       = $x+$xstep;
				$pic->{jobs}->{$jobhash}->{outputwidth}  = $width;
				$pic->{jobs}->{$jobhash}->{outputheigth} = $heigth;
				&createjob($pic->{jobs}->{$jobhash});
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
				my $jobhash = md5("$pic->{filename},$y");
				$pic->{jobs}->{$jobhash}->{filename}     = $pic->{filename};
				$pic->{jobs}->{$jobhash}->{startrow}     = $y;
				$pic->{jobs}->{$jobhash}->{endrow}       = $y+$ystep+$offset;
				$pic->{jobs}->{$jobhash}->{startcol}     = 0;
				$pic->{jobs}->{$jobhash}->{endcol}       = $width;
				$pic->{jobs}->{$jobhash}->{outputwidth}  = $width;
				$pic->{jobs}->{$jobhash}->{outputheigth} = $heigth;
				&createjob($pic->{jobs}->{$jobhash});
			}
		}

		#actually run the jobs	
		foreach my $job (keys(%{$pic->{jobs}}))
		{
			&submit($job->{startscript});
		}
	}


}

sub getCPUNumber 
{
	my @foo = qx(pbsnodes -q -l free);
	return scalar(@foo);
}
