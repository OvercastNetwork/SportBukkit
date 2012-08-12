#!/bin/bash

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# build.sh
#
# Checks out clean copies of Bukkit & CraftBukkit then applies the patches
# specified in the Bukkit/ and CraftBukkit/ directories.
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )" # directory script is in

if [ ! -d "$DIR/build" ]; then
    echo "You must run the ./update script first!"
    exit 1
fi

function apply_patches {
    project=$1
    project_lower=`echo $project | tr '[A-Z]' '[a-z]'`

    $DIR/checkout.sh $project | tee

    cd $DIR/build/$project

    echo "Applying patches..."
    for f in $DIR/$project/*.patch; do
        echo "Applying `basename $f`"
        git apply --ignore-space-change --ignore-whitespace --whitespace=nowarn --unidiff-zero $f
        if [ $? -ne 0 ]; then
            echo "Failed to apply patch...exiting now"
            exit 1
        fi
    done

    echo "Applied patches successfully!"
}

apply_patches Bukkit
apply_patches CraftBukkit
