#!/bin/bash

if !(./applyPatches.sh); then
    exit 1
else
    ./compile.sh
fi
