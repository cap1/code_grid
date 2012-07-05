#!/bin/bash
############################################################################
### Wrapper script for using the API of the FITS project                 ###
###                                                                      ###
### Installing and running FITS:                                         ###
### - Download the latest release of FITS from here:                     ###
###     http://code.google.com/p/fits/                                   ###
### - Create a directory and unzip the FITS ZIP file to the directory    ###
### - Copy the test Java class (FitsClient) to the directory             ###
### - Copy this script to the directory                                  ###
### - Run this script                                                    ###
###                                                                      ###
### [Tibor.Kalman|Daniel.Kurzawe] at gwdg dot de                         ###
### $Id: testing_fits_api.sh 11421 2011-06-24 17:01:40Z tkalman $                                                                 ###
###                                                                      ###
############################################################################

FITS_HOME=`dirname "$0"`
export FITS_HOME

# concatenate args and use eval/exec to preserve spaces in paths, options and args
args=""
for arg in "$@" ; do
	args="$args \"$arg\""
done

JCPATH=${FITS_HOME}/lib
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/droid
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/jhove
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/nzmetool
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/nzmetool/adapters
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/src/edu/harvard/hul/ois/fits
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done


### Compile FitsClient.java with the FITS libs in CLASSPATH:
javac -classpath \"$APPCLASSPATH:$FITS_HOME/xml/nlnz\" PidClient.java

### Run FitsClient with the CLASSPATH set to the FITS libs:
##java -classpath "$APPCLASSPATH:$FITS_HOME/xml/nlnz" FitsClient

#java -classpath "$APPCLASSPATH:$FITS_HOME/xml/nlnz" PidClient 11858/00-STUD-0000-0000-13D9-3 https://github.com/cap1/pke-report/blob/master/report.tex griduser9 doLhj10En4
java -classpath "$APPCLASSPATH:$FITS_HOME/xml/nlnz" PidClient -m 11858/00-STUD-0000-0000-13D9-3 authors Andere griduser9 doLhj10En4
