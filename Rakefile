task :default => [:update, :build, :compile]

task :update do
    run("scripts/update.sh")
end

task :build do
    run("scripts/build.sh")
end

task :compile do
    run("scripts/compile.sh")
end

def run(cmd)
    IO.popen(cmd) { |io| while (line = io.gets) do puts line end }

    fail if $?.exitstatus != 0
end