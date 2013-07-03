#!/bin/bash

basedir=`pwd`

function compile {
    target=build/$1

    echo "  $1 compiling..."
    cd $target
    mvn clean install
    cd $basedir

    if [ "$?" != "0" ]; then
        echo "  $1 failed to compile"
        exit 1
    else
        echo "  $1 compiled"
        echo "  JAR location: $target/target/"
    fi
}

compile Bukkit
compile CraftBukkit
