#!/bin/bash

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# update.sh
#
# Updates the cloned version of Bukkit & CraftBukkit in the build/ directory,
# or newly clones them if they do not already exist there.  This should be the
# first script run when setting up the PGM-CraftBukkit build environment.
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

if [ ! -d $DIR/build ]; then
    mkdir $DIR/build
fi

function update {
    echo "Updating $1..."
    if [ -d $DIR/build/$1 ]; then
        cd $DIR/build/$1

        $DIR/scripts/checkout.sh $1
        git pull --tags origin master
    else
        cd $DIR/build
        git clone git://github.com/Bukkit/$1.git
    fi

    if [ ! -z $2 ]; then
        git reset --hard $2
    fi
}

update Bukkit $1
update CraftBukkit $1
