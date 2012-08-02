#!/bin/bash

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# update.sh
#
# Updates the cloned version of Bukkit & CraftBukkit in the build/ directory,
# or newly clones them if they do not already exist there.  This should be the
# first script run when setting up the PGM-CraftBukkit build environment.
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

if [ ! -d "build" ]; then
    mkdir build
fi

cd build

function update {
    echo "Updating $1..."
    if [ -d $1 ]; then
        cd $1; git fetch origin master; cd ..
    else
        git clone git://github.com/Bukkit/$1.git
    fi
}

update Bukkit
update CraftBukkit
