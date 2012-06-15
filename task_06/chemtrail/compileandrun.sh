#!/bin/sh
rm chemtrail/*.class

javac chemtrail/Chemtrail.java

java -Daxis.ClientConfigFile=$GLOBUS_LOCATION/client-config.wsdd -DGLOBUS_LOCATION=$GLOBUS_LOCATION chemtrail/Chemtrail /usr/share/doc/povray/examples/advanced/biscuit.pov 640 480 foo.tga


