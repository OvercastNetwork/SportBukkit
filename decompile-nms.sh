#!/bin/bash -e
cd "$(dirname "$0")"

. util.sh
. version.sh

if [ ! -f "temp/mapped.jar" ]; then
	log_error "Could not found mapped jar. Please run remap-nms.sh and try again"
	exit 1
fi

newcleandir "temp/classes"
log_info "Extracting classes"
unzip "temp/mapped.jar" "net/minecraft/server/*" -d "temp/classes"

newcleandir work/nms-src
log_info "Decompiling using FernFlower"
java -jar builddata/bin/fernflower.jar -dgs=1 -hdc=0 -rbr=0 -asc=1 -udv=0 "temp/classes" work/nms-src

log_info "Done. Now you should run ./apply-cb-patches.sh to proceed."

