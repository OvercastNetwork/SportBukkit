require 'rubygems'
require 'git'
require 'maven/ruby/maven'

task :default => [:update, :build, :compile]

task :update, :tag do |t, args|
    args.with_defaults(:tag => 'HEAD')

    Dir.mkdir("build") if !File.directory?("build")

    update("Bukkit", args.tag)
    update("CraftBukkit", args.tag)
end

task :build do
    abort("Run update first!") if !File.directory?("build")

    build("Bukkit")
    build("CraftBukkit")
end

task :compile do
    abort("Run update first!") if !File.directory?("build")

    compile("Bukkit")
    compile("CraftBukkit")
end

# FIX ME
def run(cmd)
    IO.popen(cmd) { |io| while (line = io.gets) do puts line end }

    fail if $?.exitstatus != 0
end

def update(project, tag)
    Dir.chdir("build")

    p "Checking out " + project + " [" + tag + "]"
    if File.directory?(project)
        g = Git.open(project)
    else
        uri = "git://github.com/Bukkit/" + project + ".git"
        g = Git.clone(uri, project)
    end

    g.pull(g.remotes.first, "master")
    g.remote("origin").merge

    g.reset_hard(Git::Object::Tag.new(g, tag, tag))
    run("git clean -fd") # FIX ME

    Dir.chdir("..")
end

def build(project)
    Dir.chdir("build/" + project)

    g = Git.open(".")
    g.reset_hard
    run("git clean -fqd") # FIX ME

    p "Applying patches for: " + project

    patches = Dir.glob("../../" + project + "/*.patch").sort
    patches.each do |patch|
        p "Applying " + project + "/" + File.basename(patch)
        run("patch -Np1 --ignore-whitespace -F3 --quiet < " + patch) # FIX ME
    end

    Dir.chdir("../..")
end

def compile(project)
    Dir.chdir("build/" + project)

    mvn = Maven::Ruby::Maven.new
    fail if mvn.exec("clean install") == false

    Dir.chdir("../..")
end
