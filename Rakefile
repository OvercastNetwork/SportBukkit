task :default => [:update, :build, :compile]

task :update do
    puts `scripts/update.sh`

    fail if $?.exitstatus != 0
end

task :build do
    puts `scripts/build.sh`

    fail if $?.exitstatus != 0
end

task :compile do
    puts `scripts/compile.sh`

    fail if $?.exitstatus != 0
end