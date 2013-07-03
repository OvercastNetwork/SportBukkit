#!/bin/bash

basedir=`pwd`

function compile {
    target=build/$1

    echo "  $1 compiling..."
    cd $target

    if !(mvn clean install); then
        echo "  $1 failed to compile"
        cd $basedir
        exit $?
    else
        echo "  $1 compiled"
        echo "  JAR location: $target/target/"
    fi
    cd $basedir
}

compile Bukkit
compile CraftBukkit
