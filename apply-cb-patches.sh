#!/bin/bash -e
cd "$(dirname "$0")"

. util.sh
. version.sh

if [ ! -d base/CraftBukkit ]; then
	log_error "Could not find base/CraftBukkit folder. Please run init.sh and try again"
	exit 1
fi

log_info "Preparing patched CraftBukkit repo"
if [ ! -d work/CraftBukkit ]; then
    git clone base/CraftBukkit work/CraftBukkit
fi

pushd work/CraftBukkit
git checkout upstream
git fetch origin upstream
git reset --hard origin/upstream

log_info "Applying CraftBukkit patches"
./applyPatches.sh ../nms-src

log_info "Committing branch"
git add src/main/java/net/minecraft/server/*
git commit -m "CraftBukkit \$$(date +%s)"

popd
log_info "Done. Now you should run ./apply-sb-patches.sh to proceed."

