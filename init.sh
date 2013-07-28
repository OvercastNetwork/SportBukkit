#!/bin/bash

ORIG_PWD="$(pwd)"
cd "$(dirname "$0")"

if !([ -d base ]); then
    git submodule update --init
fi

if [ -d build ]; then
    rm build/* &> /dev/null
    if !(rmdir build); then
        echo "Unable to remove build directory"
        cd "$ORIG_PWD"
        exit $?
    fi
fi

cd "$ORIG_PWD"
