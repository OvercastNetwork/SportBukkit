#!/bin/bash

if !(./prepare-build.sh); then
    exit 1
fi
./compile.sh

