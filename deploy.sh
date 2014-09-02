#!/bin/bash

ORIG_PWD="$(pwd)"
cd "$(dirname "$0")"

function deploy {
    target=build/$1

    echo "  $1 deploying..."
    cd $target

    if !(MAVEN_OPTS=-Xmx512M mvn deploy); then
        echo "  $1 failed to deploy"
        cd "$ORIG_PWD"
        exit 1
    else
        echo "  $1 deployed"
    fi

    cd ../..
}

deploy Bukkit
deploy CraftBukkit

cd "$ORIG_PWD"
