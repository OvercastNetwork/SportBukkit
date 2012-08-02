#!/bin/bash
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

    echo "Switching to a staging branch..."
    git checkout master # can't update a branch you are already on
    git checkout -B staging origin/master --force
}

update Bukkit
update CraftBukkit


