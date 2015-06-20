#!/bin/bash

# ins verzeichnis wechseln, in dem das script steht
cd `dirname $0` || exit 1

# die Log-Datei dieses Scripts
logfile=$PWD/startPatcher.log
# das Verzeichnis, in dem der Patcher ist
directory=Patcher
# die ausführbare Datei des Patchers
execfile=patcher
# das Build-Verzeichnis
builddir=PatcherBuild

# die konfiguration ausgeben
echo "Using logfile $logfile"
echo "[$(date)] Using directory = $directory"  >$logfile
echo "[$(date)] Using execfile  = $execfile"  >>$logfile
echo "[$(date)] Using builddir  = $builddir"  >>$logfile

export "LD_LIBRARY_PATH=$PWD/$builddir:$LD_LIBRARY_PATH"

while [ true ]
do
	echo >>$logfile
	
	# bauen
	echo "[$(date)] Building the patcher" >>$logfile
	mkdir -p $builddir || exit 1
	cd $builddir || exit 1
	qmake -makefile -r "../$directory/Patcher.pro" &>>$logfile || exit 1
	make clean &>/dev/null
	make -j4 &>>$logfile || exit 1
	cd .. || exit 1
	
	# den Patcher starten
	echo "[$(date)] Starting the Patcher" >>$logfile
	echo "[$(date)] Starting the Patcher"
	"$builddir/$execfile" ${@} &>>$logfile || exit 1
done

echo "[$(date)] This should not be executed. There is no good reason to leave the while loop without exiting directly. Please contact an administrator." >>$logfile
