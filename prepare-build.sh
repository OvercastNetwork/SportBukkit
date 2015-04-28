#!/bin/bash -e
cd "$(dirname "$0")"

./init.sh
./remap-nms.sh
./decompile-nms.sh
./apply-cb-patches.sh
./apply-sb-patches.sh
