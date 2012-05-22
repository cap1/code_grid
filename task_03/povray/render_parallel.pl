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

my $cpus = &getCPUNumber();
if ($maxnodes != 0 and not($maxnodes > $cpus)) {
	$cpus = $maxnodes;
}



my @pics = ();


foreach my $if (@inputfiles)
{
	my %pic = ( filename => $if, jobs => {}); 
	my $dirname = "$outputdir" . "/" . basename($if);
	mkdir $dirname;
	push(@pics,\%pic);
}


my $xstep = 0;
my $ystep = 0;
my $xrest = 0;
my $yrest = 0;

$xstep = int($width/$cpus);
$xrest = $width % $cpus;
$ystep = int($heigth/$cpus);
$yrest = $heigth % $cpus;



sub createjob
{
	my ($filename,$startrow,$startcol,$endrow,$endcol,$outputwidth,$outputheigth) = @_;
	my $jobstring  = "$povray -I$filename -W$outputwidth -H$outputheigth ";
	$shortfn = basename($filename);
	my $dirname = "$outputdir" . "/" . $shortfn;
	   $jobstring .= "-SR$startrow -SC$startcol -ER$endrow -EC$endcol -O$dirname/";
	my $jobhash = md5($jobstring);
	mkdir "$dirname/$jobhash";
	$jobstring .= $jobhash . "/output.tga";
	
	open(FH,">$dirname/$jobhash/run.sh") || die "Could not open output file\n"; 
	print FH ("#!/bin/sh\n$jobstring\n");	
	close(FH);
	return "$dirname/$jobhash/run.sh";
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
	for ( my $y = 0; $y < $heigth-$yrest; $y+= $ystep )
	{
		for ( my $x = 0; $x < $width-$xrest; $x += $xstep )
		{
			&render($y,$y+$ystep,$x,$x+$xstep);
		}
	}
	#here be dragons
	if($xrest != 0)
	{

		&render(0,$heigth-1,$width-$xrest,$width);
	}
	if($yrest != 0)
	{
		&render($heigth-$yrest,$heigth,0,$width);
	}

}

sub getCPUNumber 
{
	my @foo = qx(pbsnodes -q -l free);
	return scalar(@foo);
}
