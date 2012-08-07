#!/bin/bash
function compile {
    cd $1
    mvn clean package install
    cd ..
}

cd build
compile Bukkit
compile CraftBukkit
