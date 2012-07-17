#!/bin/bash
rm -rf build
mkdir build
cd build

# prepare Bukkit
git clone git://github.com/Bukkit/Bukkit.git
cd Bukkit
# change pom.xml to tc.oc
sed --in-place "s#<groupId>org.bukkit</groupId>#<groupId>tc.oc</groupId>#" pom.xml
sed --in-place "s#<artifactId>bukkit</artifactId>#<artifactId>pgm-bukkit</artifactId>#" pom.xml
sed --in-place "s#<name>Bukkit</name>#<name>PGM-Bukkit</name>#" pom.xml
# apply patches
git apply ../Bukkit/*

