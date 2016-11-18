SportBukkit
===========

CraftBukkit modifications and Bukkit API additions that fix bugs, add new features, and improve the quality of life

Public build server: https://build.oc.tc/job/SportBukkit/

How To
------

Build everything from scratch: `./build.sh`
*The SportBukkit binary will be located in build/CraftBukkit/target*

Generate patched SportBukkit source code: `./prepare-build.sh`
*Generated source will be in build/Bukkit and build/CraftBukkit*

Compile SportBukkit from source: `./compile.sh`

Rebuild SportBukkit patches from source: `./rebuild-patches.sh`

Remove all generated files, **including the SportBukkit source**: `./clean.sh`


Rebasing
--------

This is roughly the process used to rebase SportBukkit to an updated upstream CraftBukkit.

* Create a branch of this repo called `rebase-(version)` e.g. `rebase-1.2.3`.
* Checkout the `upstream` branch of `build/Bukkit` and `build/CraftBukkit` and ensure they have clean work trees.
* Delete the `work` folder.
* Reset the submodules `builddata`, `base/Bukkit`, and `base/CraftBukkit` to the latest upstream master.
* In `version.sh`, update `NMS_VERSION` and `NMS_MD5` to the latest values (running remap-nms.sh with the wrong MD5 will show you the right one).
* Run `remap-nms.sh`, `decompile-nms.sh`, and `apply-cb-patches.sh`, in that order. These should all run without any conflicts.
* At this stage, you have the latest CraftBukkit present and are ready to start the actual rebasing.
  This is a good time to create an initial commit on the rebase branch, with just the submodule and `version.sh` changes.
* Run `apply-sb-patches.sh`. This will attempt to apply the SportBukkit patches in the `Bukkit` and `CraftBukkit` folders to the new CraftBukkit.
  Assuming there have been upstream changes, many of these patches will fail and you will need to resolve the conflicts.
* For each conflict, one of the repos `build/Bukkit` or `build/CraftBukkit` will be in a `git am` session, waiting on conflict resolution.
  Use the standard git merge process to resolve the conflict, and type `git am --continue` to continue applying patches.
* 
