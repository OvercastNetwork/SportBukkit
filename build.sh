#!/bin/bash

if !(./applyPatches.sh); then
    exit $?
else
    ./compile.sh
fi
