#!/bin/bash

pushd "$(dirname "$0")"

. util.sh

log_info "Preparing folder layout"
if [ ! -d work ]; then
    mkdir work
fi
if [ ! -d cache ]; then
    mkdir cache
fi
newcleandir temp

log_info "Initializing submodules"
git init
git submodule update --init

log_info "Resetting upstream repositories"
pushd base/Bukkit
git branch -f upstream
popd
pushd base/CraftBukkit
git branch -f upstream
popd

log_info "Done. Now you should run ./remap-nms.sh to proceed."
popd
