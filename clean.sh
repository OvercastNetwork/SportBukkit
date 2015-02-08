#!/bin/bash

pushd "$(dirname "$0")"

if [ -d build ]; then
    rm -rf build
    if [ -d build ]; then
        echo "Unable to remove build directory"
    fi
fi

popd
