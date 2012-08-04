#!/bin/bash

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# update.sh
#
# Updates the cloned version of Bukkit & CraftBukkit in the build/ directory,
# or newly clones them if they do not already exist there.  This should be the
# first script run when setting up the PGM-CraftBukkit build environment.
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )" # directory script is in

if [ ! -d $DIR/build ]; then
    mkdir build
fi

function update {
    echo "Updating $1..."
    if [ -d $DIR/build/$1 ]; then
        cd $DIR/build/$1

        $DIR/checkout.sh $1
        git pull origin master
    else
        git clone git://github.com/Bukkit/$1.git
    fi
}

update Bukkit
update CraftBukkit
