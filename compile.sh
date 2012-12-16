#!/bin/bash
function compile {
    cd $1
    mvn clean install
    cd ..
}

cd build
compile Bukkit
compile CraftBukkit
