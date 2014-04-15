#!/bin/bash

cd "$(dirname "$0")"
echo "Rebuilding patch files from current fork state..."

function cleanupPatches {
    cd $1
    for patch in *.patch; do
        gitver=$(tail -n 2 $patch | grep -ve "^$" | tail -n 1)
        diffs=$(git diff --staged $patch | grep -E "^(\+|\-)" | grep -Ev "(From [a-z0-9]{32,}|\-\-\- a|\+\+\+ b|.index)")

        testver=$(echo "$diffs" | tail -n 2 | grep -ve "^$" | tail -n 1 | grep "$gitver")
        if [ "x$testver" != "x" ]; then
            diffs=$(echo "$diffs" | sed 'N;$!P;$!D;$d')
        fi

        if [ "x$diffs" == "x" ] ; then
            git reset HEAD $patch >/dev/null
            git checkout -- $patch >/dev/null
        fi
    done
    cd ..
}

function rebuildPatches {
    what=base/$1
    target=build/$1
    patches=$1
    cd $target
    git format-patch --no-stat -N -o ../../$patches upstream/upstream
    cd ../..
    git add --all $patches
    cleanupPatches $patches
    echo "  Patches saved for $what to $patches"
}

rebuildPatches Bukkit
rebuildPatches CraftBukkit
