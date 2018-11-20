SportBukkit
===========

CraftBukkit modifications and Bukkit API additions that fix bugs, add new features, and improve the quality of life

File Structure
--------------

SportBukkit is a fork of CraftBukkit, starting from the submodules in the `base` folder.
The differences between CraftBukkit and SportBukkit are represented by the sequential patches in the `Bukkit` and `CraftBukkit` folders.
Conceptually, these differences are divided into *fixes* and *features*.

Fixes are relatively small and have the potential to change or go away with future upstream changes.
Every patch except the final one in each list contains a fix.

Feature changes are improvements and extensions to the Bukkit API, intended to be permanent.
These are contained entirely in a single patch at the end of each sequence, called simply "SportBukkit".

For readability purposes, a copy of the final *patched* SportBukkit source code is stored in the `snapshot` folder.
This code is updated automatically by scripts, and should not be manually edited or compiled.


Requirements
------------

To build SportBukkit, the following will need to be installed and available from your shell:

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) version 121 or later (older versions *might* work, but some are known not to)
* [Git](https://git-scm.com)
* [Maven](https://maven.apache.org)
* [Ruby](https://www.ruby-lang.org/) version 2.1 or later

To build on Windows you'll need to use the git bash console.

How To
------

See all available tasks: `rake -T`

Build everything: `rake`
*The SportBukkit binary will be located in build/CraftBukkit/target*

Generate patched SportBukkit source code: `rake apply`
*Generated source will be in build/Bukkit and build/CraftBukkit*

Compile SportBukkit from source: `rake compile`

Rebuild SportBukkit patches from source: `rake generate`

Remove all intermediate files: `rake clean`


Rebasing
--------

This is roughly the process used to rebase SportBukkit to an updated upstream CraftBukkit.

* Create a branch of this repo called `rebase-(version)` e.g. `rebase-1.2.3`.
* Checkout the `upstream` branch of `build/Bukkit` and `build/CraftBukkit` and ensure they have clean work trees.
* Delete the `work` folder.
* Reset the submodules `builddata`, `base/Bukkit`, and `base/CraftBukkit` to the latest upstream master.
* In `version.rb`, update `NMS_VERSION` and `NMS_MD5` to the latest values (running `rake deobf` with the wrong MD5 will show you the right one).
* Run `rake deobf`, `rake decompile`, and `rake craftbukkit`, in that order. These should all run without any conflicts.
* At this stage, you have the latest CraftBukkit present and are ready to start the actual rebasing.
  This is a good time to create an initial commit on the rebase branch, with just the submodule and `version.rb` changes.
* Run `rake apply`. This will attempt to apply the SportBukkit patches in the `Bukkit` and `CraftBukkit` folders to the new CraftBukkit.
  Assuming there have been upstream changes, many of these patches will fail and you will need to resolve the conflicts.
* For each conflict, one of the repos `build/Bukkit` or `build/CraftBukkit` will be in a `git am` session, waiting on conflict resolution.
  Use the standard git merge process to resolve the conflict, and type `git am --continue` to continue applying patches.
* Along the way, you can run `rake generate` to capture your progress in the patch files.
* When you get to the end of the Bukkit patches, rebuild them and then run `rake apply` again to continue with the CraftBukkit patches.
* The first CraftBukkit patch is strictly for importing NMS files verbatim. To recreate this patch, just copy the latest NMS version of each file
  in the patch from `work/nms-src` to `build/CraftBukkit/src/main/java`. This patch should *always* be updated, even if there are no merge conflicts.
* The second CraftBukkit patch is strictly for fixing decompile errors. This patch should only make minimal changes to the files in the first patch to make them compile.
