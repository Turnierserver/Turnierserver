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
        elif [ -r "$file" ]
        then
            echo "Compiling $file"
            javac -d "$3" -cp "$1" "$file" || exit 1
        fi
    done
}

#### BUILDPROJEKT ###############################################################################################################

function buildProject ()
{
    mkdir -p $2/bin
    buildDir "$1" $2/src $2/bin || exit 1
}

#### MAIN #######################################################################################################################

# altes build löschen
if [ -d build ]
then
    rm -rf build
fi

# die projekte, die gebaut werden
projects="Backend CompilerLib Game-Logic NetworkingLib Worker"

# den classpath finden
cp="."
for project in $projects
do
    cp="$cp:$project:$project/bin:$project/src"
done
for dir in lib/*
do
    if [ -d $dir ]
    then
        for jar in $dir/*.jar
        do
            cp="$cp:$jar"
        done
    fi
done
echo $cp

# jedes projekt davon einzeln bauen
for project in $projects
do
    buildProject $cp $project || exit 1
done
