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
    check_build
    build("Bukkit")
    build("CraftBukkit")
end

task :build_spigot do
    check_build
    build("CraftBukkit", "Spigot")
end

task :compile do
    check_build
    compile("Bukkit")
    compile("CraftBukkit")
end

task :deploy do
    check_build
    deploy("Bukkit")
    deploy("CraftBukkit")
end
# FIX ME
def run(cmd)
    IO.popen(cmd) { |io| while (line = io.gets) do puts line end }

    fail if $?.exitstatus != 0
end

def check_build
    abort("Run update first!") if !File.directory?("build")
end

def update(project, tag)
    Dir.chdir("build")

    p "Checking out " + project + " [" + tag + "]"
    if File.directory?(project)
        g = Git.open(project)
    else
        uri = "https://github.com/Bukkit/" + project + ".git"
        g = Git.clone(uri, project)
    end

    g.reset_hard(Git::Object::Tag.new(g, tag, tag))
    run("git clean -fd") # FIX ME

    g.pull(g.remotes.first, "master")
    g.remote("origin").merge

    Dir.chdir("..")
end

def build(project, dir=nil)
    dir = project if dir == nil

    Dir.chdir("build/" + project)

    g = Git.open(".")
    g.reset_hard
    run("git clean -fqd") # FIX ME

    p "Applying patches for: " + dir

    patches = Dir.glob("../../" + dir + "/*.patch").sort
    patches.each do |patch|
        p "Applying " + dir + "/" + File.basename(patch)
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

def deploy(project)
    Dir.chdir("build/" + project)

    mvn = Maven::Ruby::Maven.new
    fail if mvn.exec("deploy") == false

    Dir.chdir("../..")
end
