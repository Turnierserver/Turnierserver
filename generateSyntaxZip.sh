#!/bin/bash

rm *.xml style.zip &>/dev/null

javac -encoding utf8 GenerateSyntaxZip.java || exit 1
time java GenerateSyntaxZip

unzip syntax.zip &>/dev/null
cat ini.xml
rm *.xml
