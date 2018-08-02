#!/bin/bash

while [[ "$#" > 0 ]]; do case $1 in
  -t) WFLY_HOME="$2"; shift;;
  -j) JCO_HOME="$2"; shift;;
  *) echo "Unknown parameter passed: $1"; exit 1;;
esac; shift; done

if [[ "x$WFLY_HOME" = "x" || "x$JCO_HOME" = "x" ]]; then
    echo 'Use:';
    echo '';
    echo './install-sapjco-module.sh -t $WILDFLY_HOME -j $JCO_HOME';
    echo '';
    echo 'to setup the sapjco3 module. Where:';
    echo ' - WILDFLY_HOME denotes the path to your Wildfly installation, e.g. /opt/wildfly-12.0.0.Final.'
    echo ' - JCO_HOME denotes the download folder of your SAP JCo3 connector, e.g. ~/Downloads/JCo3.';
    echo '';
    exit 1;
fi;

if [[ ! -d $WFLY_HOME || ! -x $WFLY_HOME ]]; then
   echo "$WFLY_HOME cannot be read.";
   exit 1;
fi;

if [[ ! -d $JCO_HOME || ! -x $JCO_HOME ]]; then
   echo "$JCO_HOME cannot be read."
   exit 1;
fi;

if [[ ! -r $JCO_HOME/sapjco3.jar ]]; then
   echo "Cannot find sapjco3.jar in $JCO_HOME...";
   exit 1;
fi;

echo "Installing module base...";
cp -av modules $WFLY_HOME;

if [[ -r $JCO_HOME/sapjco3.jar ]]; then
   echo "Installing sapjco3.jar into module...";
   cp -av $JCO_HOME/sapjco3.jar $WFLY_HOME/modules/system/layers/base/com/sap/conn/main;
fi;

if [[ -r $JCO_HOME/libsapjco3.so ]]; then
   echo "Installing libsapjco3.so into module...";
   mkdir -p $WFLY_HOME/modules/system/layers/base/com/sap/conn/main/lib/linux-x86_64;
   cp -av $JCO_HOME/libsapjco3.so $WFLY_HOME/modules/system/layers/base/com/sap/conn/main/lib/linux-x86_64;
fi;

if [[ -r $JCO_HOME/libsapjco3.dll ]]; then
   echo "Installing libsapjco3.dll into module...";
   mkdir -p $WFLY_HOME/modules/system/layers/base/com/sap/conn/main/lib/win-x86_64;
   cp -av $JCO_HOME/libsapjco3.dll $WFLY_HOME/modules/system/layers/base/com/sap/conn/main/lib/win-x86_64;
fi;

echo "Installed SAP JCo3 module into $WFLY_HOME/modules/system/layers/base/com/sap/conn/main:";
ls -lR $WFLY_HOME/modules/system/layers/base/com/sap/conn/main