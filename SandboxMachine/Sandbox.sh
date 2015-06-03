#!/bin/bash

PATH="../build-SandboxHelper-Desktop-Release:$PATH"
if [ "$1" == "gdb" ]; then
	./startGdb.sh
else
	../build-SandboxMachine-Desktop-Debug/sandboxd ../../Turnierserver-Config/SandboxMachine/SandboxMachine.ini
fi
