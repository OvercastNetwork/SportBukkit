task :default => [:update, :build, :compile]

task :update, :tag do |t, args|
    args.with_defaults(:tag => 'HEAD')
    run("scripts/update.sh #{args.tag}")
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