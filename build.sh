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

    $DIR/checkout.sh $project

    cd $DIR/build/$project

    echo "Applying patches..."
    git apply --ignore-space-change --ignore-whitespace --whitespace=fix $DIR/$project/*.patch

    if [ $? -ne 0 ]; then
        echo "Failed to apply patches...exiting now"
        exit 1
    fi
}

apply_patches Bukkit
apply_patches CraftBukkit
