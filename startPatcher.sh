#!/bin/bash

# ins verzeichnis wechseln, in dem das script steht
cd `dirname $0` || exit 1

# die Log-Datei dieses Scripts
logfile=startPatcher.log
# das Verzeichnis, in dem der Patcher ist
directory=.
# das Verzeichnis, von dem aus ich aus dem Verzeichnis des Patchers ins git-root komme
dirback=.
# die main-Klasse des Patchers
mainclass=org.pixelgaffer.turnierserver.patcher.Patcher

# die konfiguration ausgeben
echo "Using logfile $logfile"
echo "[$(date)] Using directory = $directory"  >$logfile
echo "[$(date)] Using dirback   = $dirback"   >>$logfile
echo "[$(date)] Using mainclass = $mainclass" >>$logfile

while [ true ]
do
	cwd=$PWD
	echo >>$logfile
	
	# das Patcher-Verzeichnis betreten
	cd $directory 2&>1 >>$logfile || exit 1
	# aufräumen
	echo "[$(date)] Cleaning old package of the patcher" >>$logfile
	mvn clean 2>&1 >>$logfile || exit 1
	# bauen
	echo "[$(date)] Building the patcher" >> $logfile
	mvn package 2>&1 >>$logfile || exit 1
	
	# ins git-root gehen
	cd $dirback 2>&1 >>$logfile || exit 1
	# den Patcher starten
	echo "[$(date)] Starting the Patcher" >>$logfile
	java $mainclass ${@} 2>&1 >>$logfile || exit 1
	
	# zurück ins aufruf-verzeichnis gehen
	cd $cwd 2>&1 >>$logfile || exit 1
done

echo "[$(date)] This should not be executed. There is no good reason to leave the while loop without exiting directly. Please contact an administrator." >>$logfile
