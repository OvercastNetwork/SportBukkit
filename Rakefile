require 'time'
require 'rake/clean'

require_relative 'version'
require_relative 'utils'

BASE                = "base"
WORK                = "work"
CACHE               = "cache"
TEMP                = "temp"
BUILD               = "build"
SNAPSHOT            = "snapshot"

CB_BASE             = "#{BASE}/CraftBukkit"
CB_WORK             = "#{WORK}/CraftBukkit"

DATA                = "builddata"

SPECIAL_SOURCE      = "#{DATA}/bin/SpecialSource.jar"
SPECIAL_SOURCE_2    = "#{DATA}/bin/SpecialSource-2.jar"
FERNFLOWER          = "#{DATA}/bin/fernflower.jar"

CLASS_MAPPINGS      = "#{DATA}/mappings/bukkit-#{NMS_VERSION}-cl.csrg"
MEMBER_MAPPINGS     = "#{DATA}/mappings/bukkit-#{NMS_VERSION}-members.csrg"
ACCESS_MAPPINGS     = "#{DATA}/mappings/bukkit-#{NMS_VERSION}.at"
PACKAGE_MAPPINGS    = "#{DATA}/mappings/package.srg"

CLASS_JAR           = "#{TEMP}/cl.jar"
MEMBER_JAR          = "#{TEMP}/member.jar"
REMAPPED_JAR        = "#{TEMP}/mapped.jar"

NMS_CLASSES         = "#{TEMP}/classes"
NMS_SRC             = "#{WORK}/nms-src"


# BuildData

task :init => "data:init"


namespace :data do
    desc "Prepare BuildData submodule"
    task :init => DATA

    Git::Rake.repo DATA do
        Git.submodule_update DATA
    end
end


# Bukkit/CraftBukkit

directory BUILD

def modular_tasks(id:, name:, artifact:, work: nil)
    base = File.join(BASE, name)

    work ||= base
    work_abs = File.absolute_path(work)

    patches = name
    patches_abs = File.absolute_path(patches)
    patches_glob = File.join(patches, "*.patch")

    build = File.join(BUILD, name)
    jar = File.join(build, "target", "#{artifact}-#{NMS_VERSION}-R0.1-SNAPSHOT.jar")

    snapshot = File.join(SNAPSHOT, name)
    snapshot_nms = File.join(snapshot, "nms-patches")

    patched = -> { Dir.chdir(build) { "SportBukkit" == Git.head_commit_message } }

    CLEAN.include(File.join(build, "target"))

    task :default => jar

    desc "Prepare submodules"
    task :init => "#{id}:init"

    desc "Apply patches"
    task :apply => "#{id}:apply"

    desc "Compile everything"
    task :compile => "#{id}:compile"

    desc "Deploy everything"
    task :deploy => "#{id}:deploy"

    desc "Generate patches"
    task :generate => "#{id}:generate"

    desc "Update snapshot"
    task :snapshot => "#{id}:snapshot"

    namespace id do
        desc "Prepare #{name} submodule"
        task :init => base

        Git::Rake.repo base do
            Git.submodule_update base
            Dir.chdir base do
                Git.branch "upstream"
            end
        end

        Git::Rake.repo build => work do
            Git.clone from: work, to: build
        end

        desc "Apply patches to #{name}"
        task :apply => [work, build] do
            Dir.chdir build do
                Git.remote_add remote: "upstream", url: work_abs
                Git.assert_clean_work_tree
                Git.reset remote: "upstream", branch: "upstream"
                Git.apply patches_abs
            end
        end

        desc "Compile #{name}"
        task :compile => [:compile_clean, jar]

        task :compile_clean do
            clean jar
        end

        file jar => build do
            unless patched[]
                Rake::Task["#{id}:apply"].invoke
            end
            Dir.chdir build do
                info "Compiling #{build}"
                maven :clean, :install
            end
        end

        desc "Deploy #{name}"
        task :deploy => jar do
            Dir.chdir build do
                info "Deploying #{build}"
                maven :deploy
            end
        end

        desc "Generate patches for #{name}"
        task :generate => build do
            complete = patched[]
            if complete
                info "Generating complete patch set for #{name}"
                sh "rm #{patches_glob}"
            else
                info "Generating partial patch set for #{name}"
            end

            Dir.chdir build do
                Git.generate_patches from: "upstream/upstream", patches: patches_abs
            end

            sh "git", "add", "--all", patches

            if complete
                Rake::Task["#{id}:snapshot"].execute
            end
        end

        desc "Update snapshot for #{name}"
        task :snapshot => build do
            unless patched[]
                error "Cannot update snapshot because #{name} is not fully patched"
                raise
            end

            info "Updating snapshot in #{snapshot}"
            Git.export from: build, to: snapshot

            if File.exists? snapshot_nms
                nms_src = File.absolute_path(NMS_SRC)
                Dir.chdir snapshot do
                    sh "./makePatches.sh", nms_src
                    sh "rm -rf src/main/java/net/minecraft"
                    sh "rmdir src/main/java/net" do |ok, res| end
                end
            end

            sh "git", "add", "--all", snapshot
        end
    end
end

modular_tasks id:       "bukkit",
              name:     "Bukkit",
              artifact: "sportbukkit-api"

modular_tasks id:       "cb",
              name:     "CraftBukkit",
              artifact: "sportbukkit",
              work:     File.join(WORK, "CraftBukkit")


# Deobfuscate/Decompile

[WORK, CACHE, TEMP].each{|f| CLEAN.include(f) }

directory WORK
directory CACHE
directory TEMP
directory NMS_CLASSES
directory NMS_SRC

desc "Deobfuscate NMS classes"
task :deobf => [:deobf_clean, REMAPPED_JAR]

task :deobf_clean do
    clean CLASS_JAR, MEMBER_JAR, REMAPPED_JAR
end

file REMAPPED_JAR => [DATA, CACHE, TEMP] do
    download(file: NMS_JAR, url: NMS_URL, md5: NMS_MD5)

    info "Creating cl-mapped jar"
    sh "java", "-jar", SPECIAL_SOURCE_2, "map",
       "-i", NMS_JAR,
       "-m", CLASS_MAPPINGS,
       "-o", CLASS_JAR

    info "Creating member-mapped jar"
    sh "java", "-jar", SPECIAL_SOURCE_2, "map",
       "-i", CLASS_JAR,
       "-m", MEMBER_MAPPINGS,
       "-o", MEMBER_JAR

    info "Creating final mapped jar"
    sh "java", "-jar", SPECIAL_SOURCE,
       "--kill-lvt",
       "-i", MEMBER_JAR,
       "--access-transformer", ACCESS_MAPPINGS,
       "-m", PACKAGE_MAPPINGS,
       "-o", REMAPPED_JAR

    info "Installing in Maven repository"
    sh "mvn", "install:install-file",
       %{-Dfile=#{REMAPPED_JAR}},
       "-Dpackaging=jar",
       "-DgroupId=org.spigotmc",
       "-DartifactId=minecraft-server",
       %{-Dversion="#{NMS_VERSION}-SNAPSHOT"}
end

desc "Decompile NMS classes"
task :decompile => [:decompile_clean, NMS_SRC]

task :decompile_clean do
    clean NMS_CLASSES, NMS_SRC
end

file NMS_SRC => REMAPPED_JAR do
    info "Extracting NMS classes"
    sh "unzip", "-q", REMAPPED_JAR, "net/minecraft/server/*", "-d", NMS_CLASSES

    info "Decompiling NMS classes"
    sh "java", "-jar", FERNFLOWER,
       "-dgs=1", "-hdc=0", "-asc=1", "-udv=0",
       NMS_CLASSES, NMS_SRC
end

desc "Apply CraftBukkit patches to NMS source"
task :craftbukkit => [:craftbukkit_clean, CB_WORK]

task :craftbukkit_clean do
    clean CB_WORK
end

Git::Rake.repo CB_WORK => [CB_BASE, NMS_SRC] do
    Git.clone from: CB_BASE, to: CB_WORK

    info "Applying CraftBukkit patches"
    Dir.chdir CB_WORK do
        sh "git checkout upstream"
        sh "git fetch origin upstream"
        sh "git reset --hard origin/upstream"
        sh "./applyPatches.sh ../nms-src"
        sh "git add src/main/java/net/minecraft/server/*"
        sh "git", "commit", "-m", "CraftBukkit #{Time.now.utc.iso8601}"
    end
end
