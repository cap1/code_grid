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

	$xstep = int($width/$cpus);
	$xrest = $width % $cpus;
	$ystep = int($heigth/$cpus);
	$yrest = $heigth % $cpus;

}

sub getCPUNumber 
{
	my @foo = qx(pbsnodes -q -l free);
	return scalar(@foo);
}
