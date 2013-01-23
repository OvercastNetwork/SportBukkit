#!/bin/bash

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# checkout.sh
#
# Checks out a clean copy of the specified repository (Bukkit or CraftBukkit).
# Usage: ./checkout.sh Bukkit or ./checkout.sh CraftBukkit
# Note: This is meant to be run by the other shell scripts, not manually in
#       most circumstances.
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."

if [ ! -d "$DIR/build" ]; then
    echo "You must run ./update first!"
    exit 1
fi

cd "$DIR/build/$1"

echo "Switching to a staging branch for $1..."
git reset --hard # reset modified files
git clean -fd --quiet . # reset untracked files
