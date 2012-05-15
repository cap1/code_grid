#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;

my $inputfile = "";
my $width = 0;
my $heigth = 0;
my $outputfile = "";
my $maxnodes  = 0;


my $result = GetOptions (  "input=s" => \$inputfile, 
			"width=i" => \$width, 
			"height=i" => \$heigth,
			"ouput=s" => \$outputfile,
			"maxnodes=i" => \$maxnodes,
		     );

my $cpus;
if ($maxnodes != 0) {
	$cpus = $maxnodes;
}
else {
	$cpus = &getCPUNumber();
}

my $xstep = 0;
my $ystep = 0;
my $xrest = 0;
my $yrest = 0;

$xstep = int($width/$cpus);
$xrest = $width % $cpus;
$ystep = int($heigth/$cpus);
$yrest = $heigth % $cpus;



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

#render function
sub render 
{
	my ($SRn, $ERn, $SCn, $ECn) = @_;
	for (my $i = $SRn; $i<$ERn;$i++)
	{
		for (my $j=$SCn; $j<$ECn; $j++)
		{

		}
	}

}

sub getCPUNumber 
{
	my @foo = qx(pbsnodes -q -l free);
	return scalar(@foo);
}
