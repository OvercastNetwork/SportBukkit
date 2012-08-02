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
    echo "Editing the pom.xml for $project..."
    
    # change pom.xml to tc.oc
    sed --in-place "s#<groupId>org.bukkit</groupId>#<groupId>tc.oc</groupId>#" pom.xml
    sed --in-place "s#<artifactId>$project_lower</artifactId>#<artifactId>pgm-$project_lower</artifactId>#" pom.xml
    sed --in-place "s#<name>$project</name>#<name>PGM-$project</name>#" pom.xml
    # apply patches
    git apply $DIR/$project/*.patch
}

apply_patches Bukkit
apply_patches CraftBukkit
