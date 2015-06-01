#!/bin/sh

if [ -d target ]
then
	rm -rf target
fi
mkdir -p target/classes
mkdir -p target/generated-sources/annotations

javac \
	-d /home/dominic/git/Turnierserver/NetworkingLib/target/classes\
	-classpath /home/dominic/git/Turnierserver/NetworkingLib/target/classes:/home/dominic/.m2/repository/it/sauronsoftware/ftp4j/ftp4j/1.7.2/ftp4j-1.7.2.jar:/home/dominic/.m2/repository/naga/naga/1.1/naga-1.1.jar:/home/dominic/.m2/repository/org/projectlombok/lombok/1.16.4/lombok-1.16.4.jar:/home/dominic/.m2/repository/com/google/code/gson/gson/2.3.1/gson-2.3.1.jar:\
	-sourcepath /home/dominic/git/Turnierserver/NetworkingLib/src/main/java:\
	-s /home/dominic/git/Turnierserver/NetworkingLib/target/generated-sources/annotations\
	-g\
	-target 1.8\
	-source 1.8\
	/home/dominic/git/Turnierserver/NetworkingLib/src/main/java/org/pixelgaffer/turnierserver/networking/messages/*.java
