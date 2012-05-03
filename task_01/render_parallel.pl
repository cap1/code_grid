#!/usr/bin/perl
use strict;
use warnings;
use Curses;

if(scalar(@ARGV) != 2)
{
	die "Program needs 2 parameters";
}

my $width = shift(@ARGV);
my $height = shift(@ARGV);
my $no_gfx = 0;
my $instancecount = 0;
my $cpus = 4;

my $xstep = 0;
my $ystep = 0;
my $xrest = 0;
my $yrest = 0;

#if($width % $cpus != 0 or $height % $cpus !=0)
#{
#		$cpus--;
#}


$xstep = int($width/$cpus);
$xrest = $width % $cpus;
$ystep = int($height/$cpus);
$yrest = $height % $cpus;

#my $xstep = 5;
#my $ystep = 5;
#my $cpus = getCPUNumber();
#$cpus++;


&main();

sub main
{
	init();
	addstr($height+3,0,"CPUS: $cpus XSTEP: $xstep YSTEP: $ystep XREST: $xrest YREST: $yrest");
	for ( my $y = 0; $y < $height-$yrest; $y+= $ystep )
	{
		for ( my $x = 0; $x < $width-$xrest; $x += $xstep )
		{
			$instancecount++;
			render($y,$y+$ystep,$x,$x+$xstep,$instancecount);
			if($instancecount == $cpus)
			{
				$instancecount = 0;
			}
		}
	}
	if($xrest != 0)
	{

		render(0,$height-1,$width-$xrest,$width,++$instancecount);
	}
	if($yrest != 0)
	{
		render($height-$yrest,$height,0,$width,++$instancecount);
	}
	cleanup();

}

#==================================================================================

sub cleanup
{
	unless ($no_gfx)
	{
		refresh();
		getch();
		endwin();
	}
}


sub init
{
	unless ($no_gfx)
	{
		initscr();
		start_color();
		init_pair(1, COLOR_RED, COLOR_BLACK);
		init_pair(2, COLOR_GREEN, COLOR_BLACK);
		init_pair(3, COLOR_MAGENTA, COLOR_BLACK);
		init_pair(4, COLOR_YELLOW, COLOR_BLACK);
		init_pair(5, COLOR_BLUE, COLOR_BLACK);
		init_pair(6, COLOR_CYAN, COLOR_BLACK);
		init_pair(7, COLOR_WHITE, COLOR_BLACK);
		clear();
		noecho();
	}
}


#not used so far
sub calculate_stepwidths
{
	my ($width,$height) = @_;
	my $square = $width*$height;
	

}



#render function
#Note, the $instance variable is not needed for the actual parallelization. It is just used for making it look nicely.
sub render 
{
	my ($SRn, $ERn, $SCn, $ECn,$instance) = @_;
	for (my $i = $SRn; $i<$ERn;$i++)
	{
		for (my $j=$SCn; $j<$ECn; $j++)
		{
			attron(COLOR_PAIR($instance));
			addstr($i,$j,"X");
			attroff(COLOR_PAIR($instance));
		}
	}

}

sub getCPUNumber 
{
	my $max = shift;
	if(defined($max) && $max < 7)
	{
		return int(rand($max)) + 1;
	}
	else
	{
		return int(rand(6)) + 1;
	}
}
