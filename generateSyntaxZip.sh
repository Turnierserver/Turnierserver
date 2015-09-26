#!/bin/bash

rm style.zip &>/dev/null

javac -encoding utf8 GenerateSyntaxZip.java || exit 1
java GenerateSyntaxZip
