#!/bin/bash

ORIG_PWD="$(pwd)"
cd "$(dirname "$0")"
echo "  Rebuilding Forked projects.... "

function applyPatches {
    what=base/$1
    target=build/$1
    patches=$1

    cd $what
    git branch -f upstream >/dev/null
    cd ../../
    if [ ! -d  $target ]; then
        git clone $what $target -b upstream
    fi

    cd $target
    echo "  Resetting $target to $what..."
    git remote rm upstream 2>/dev/null 2>&1
    git remote add upstream ../../$what >/dev/null 2>&1
    git checkout master >/dev/null 2>&1
    git fetch upstream >/dev/null 2>&1
    git reset --hard upstream/upstream

    echo "  Applying patches to $target..."
    git am --abort

    if !(git am --3way ../../$patches/*.patch); then
        echo "  Something did not apply cleanly to $target."
        echo "  Please review above details and finish the apply then"
        echo "  save the changes with rebuildPatches.sh"
        cd "$ORIG_PWD"
        exit $?
    else
        echo "  Patches applied cleanly to $target"
    fi

    cd ../..
}

applyPatches Bukkit
applyPatches CraftBukkit

cd "$ORIG_PWD"
