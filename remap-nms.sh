#!/bin/bash -e
cd "$(dirname "$0")"

. util.sh
. version.sh

downloadfile "${NMS_JAR}" "${NMS_URL}" "${NMS_MD5}"

log_info "Creating cl-mapped jar"
java -jar builddata/bin/SpecialSource-2.jar map -i "${NMS_JAR}" -m "builddata/mappings/bukkit-${NMS_VERSION}-cl.csrg" -o "temp/cl.jar"

log_info "Creating member-mapped jar"
java -jar builddata/bin/SpecialSource-2.jar map -i "temp/cl.jar" -m "builddata/mappings/bukkit-${NMS_VERSION}-members.csrg" -o "temp/member.jar"

log_info "Creating final mapped jar"
java -jar builddata/bin/SpecialSource.jar -i "temp/member.jar" --access-transformer "builddata/mappings/bukkit-${NMS_VERSION}.at" -m "builddata/mappings/package.srg" -o "temp/mapped.jar"

log_info "Installing in Maven repository"
mvn install:install-file -Dfile="temp/mapped.jar" -Dpackaging=jar -DgroupId=org.spigotmc -DartifactId=minecraft-server -Dversion="${NMS_VERSION}-SNAPSHOT"

log_info "Done. Now you should run ./decompile-nms.sh to proceed."

