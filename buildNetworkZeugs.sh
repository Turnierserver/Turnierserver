#!/bin/bash

#### BUILDDIR ###################################################################################################################

function buildDir ()
{
    echo "Entering directory $2"
    for file in "$2"/*
    do
        if [ -d "$file" ]
        then
            buildDir "$1" "$file" "$3"
        elif [ -r "$file" ] && [[ "$file" == *.java ]]
        then
            
            echo "Compiling $file"
            javac -d "$3" -cp "$1" "$file" || exit 1
        fi
    done
}

#### BUILDPROJEKT ###############################################################################################################

function buildProject ()
{
    # delombok
#    echo "Delomboking project $3"
#    mkdir -p $3/delombok
#    java -jar lib/lombok/lombok.jar delombok -d $3/delombok $3/src &>/dev/null
    
    # build
    echo "Building project $3"
    mkdir -p $3/bin
#    buildDir "$1" $3/delombok $3/bin || exit 1
    buildDir "$1" $3/src $3/bin || exit 1
    
    # jar erstellen
    echo "Building JAR for $3"
    echo "Manifest-Version: 1.0" > build/.$3.mf
    echo "Created-By: buildNetworkZeugs.sh" >> build/.$3.mf
    echo "Class-Path: $2" >> build/.$3.mf
    if [ $3 == Backend ]
    then
        echo "Main-Class: org.pixelgaffer.turnierserver.backend.BackendMain" >> build/.$3.mf
    elif [ $3 == Worker ]
    then
        echo "Main-Class: org.pixelgaffer.turnierserver.worker.WorkerMain" >> build/.$3.mf
    fi
    ( cd $3/bin && jar cfm ../../build/$3.jar ../../build/.$3.mf org/pixelgaffer )
}

#### MAIN #######################################################################################################################

# altes build löschen
if [ -d build ]
then
    rm -rf build
fi

# neues build-verzeichnis anlegen
mkdir -p build || exit 1

# die projekte, die gebaut werden
projects="Backend CompilerLib Game-Logic NetworkingLib Worker"

# den classpath finden
jarcp="."
cp="."
for project in $projects
do
    cp="$cp:$project:$project/bin:$project/src"
    jarcp="$jarcp $project.jar"
done
for dir in lib/*
do
    if [ -d $dir ]
    then
        for jar in $dir/*.jar
        do
            cp="$cp:$jar"
            cp "$jar" build/
            jarcp="$jarcp  `basename "$jar"`"
        done
    fi
done
#echo $cp
#echo $jarcp

# jedes projekt davon einzeln bauen
for project in $projects
do
    buildProject "$cp" "$jarcp" $project || exit 1
done
