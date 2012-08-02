#!/bin/bash

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# checkout.sh
#
# Checks out a clean copy of the specified repository (Bukkit or CraftBukkit).
# Usage: ./checkout.sh Bukkit or ./checkout.sh CraftBukkit
# Note: This is meant to be run by the other shell scripts, not manually in
#       most circumstances.
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )" # directory script is in

if [ ! -d "$DIR/build" ]; then
    echo "You must run ./update first!"
    exit 1
fi

cd "$DIR/build/$1"

echo "Switching to a staging branch for $1..."
git checkout master # can't update a branch you are already on
git checkout -B staging origin/master --force
