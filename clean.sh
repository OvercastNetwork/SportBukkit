#!/bin/bash

function clean {
    if [ -d $1 ]; then
        rm -rf $1
        if [ -d $1 ]; then
            echo "Unable to remove $1 directory"
        fi
    fi
}

pushd "$(dirname "$0")"

clean temp
clean cache
clean build
clean work

popd
