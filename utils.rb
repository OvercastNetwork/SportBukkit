require 'digest'
require 'pathname'
require 'shellwords'
require 'rake'

module Ansi
    extend self

    COLORS = [:black, :red, :green, :yellow, :blue, :purple, :aqua]

    def color(code, text = nil)
        if text
            "#{color(code)}#{text}#{reset}"
        else
            "\e[#{code}m"
        end
    end

    def reset
        color(0)
    end

    COLORS.each_with_index do |name, code|
        define_method name do |text = nil|
            color(30 + code, text)
        end
    end
end

def info(msg)
    puts "#{Ansi.green "[INFO]"} #{Ansi.aqua msg}"
end

def warning(msg)
    puts "#{Ansi.yellow "[WARNING]"} #{Ansi.aqua msg}"
end

def error(msg)
    raise "#{Ansi.red "[ERROR]"} #{Ansi.aqua msg}"
end

def relative_path(path, from: nil)
    pn = Pathname.new(path)
    if pn.absolute?
        pn = pn.relative_path_from(from || Pathname.pwd)
    end
    pn.to_s
end

def clean(*paths)
    paths.each do |path|
        if File.exists?(path)
            sh "rm", "-rf", path
        end
    end
end

def download(file:, url:, md5:)
    if File.exists?(file)
        cached_md5 =  Digest::MD5.file(file).hexdigest
        if md5 == Digest::MD5.file(file).hexdigest
            info "Using cached #{file}"
            return
        end
        warning "Found a cached but corrupted version of #{file} (expected MD5 #{md5}, got #{cached_md5})"
        FileUtils.rm(file)
    end

    info "Downloading #{url}"
    sh "curl", "-o", file, url

    actual_md5 = Digest::MD5.file(file).hexdigest
    unless md5 == actual_md5
        error "Downloaded file is corrupted (expected MD5 #{md5}, got #{actual_md5}"
    end
end

def maven(*goals)
    system({"MAVEN_OPTS" => [*ENV["MAVEN_OPTS"], "-Xmx512M"].join(' ')},
           "mvn", *goals.map(&:to_s))
end

module Git
    extend self
    extend FileUtils

    def init(repo)
        unless File.exists?(File.join(repo, ".git"))
            info "Initializing git in #{repo}"
            sh "git", "init", repo
        end
    end

    def clone(from:, to:)
        unless File.exists?(File.join(to, ".git"))
            info "Cloning #{from} to #{to}"
            sh "git", "clone", from, to
        end
    end

    def submodule_update(path)
        info "Checking out submodule #{path}"
        sh "git", "submodule", "update", "--init", path
    end

    def branch(branch)
        sh "git", "checkout", branch do |ok, res|
            unless ok
                sh "git", "branch", "-f", branch
                sh "git", "checkout", branch
            end
        end
    end

    def remote_add(remote:, url:)
        unless `git remote`.split.include?(remote)
            info "Adding remote #{remote} at #{url}"
            sh "git", "remote", "add", remote, url
        end
    end

    def reset(remote:, branch:)
        ref = "refs/remotes/#{remote}/#{branch}"
        info "Resetting to #{ref}"
        sh "git", "fetch", remote, "+#{branch}:#{ref}"
        sh "git", "reset", "--hard", "#{remote}/#{branch}"
    end

    def apply_patches(dir:, list:)
        patches = File.read(list).lines
            .select{|line| line =~ /\S/ }
            .map{|name| relative_path(File.join(dir, "#{name.chomp}.patch")) }

        info "Applying #{patches.size} patches"

        if `git status` =~ /You are in the middle of an am session/
            sh "git am --abort"
        end

        sh "git clean -df"
        sh "git am --3way --ignore-whitespace --committer-date-is-author-date #{patches.join(' ')}" do |ok, res|
            unless ok
                error "A patch did not apply cleanly"
            end
        end
    end

    def assert_clean_work_tree
        unless FORCE
            sh "git update-index -q --ignore-submodules --refresh"
            ["git diff-files --quiet --ignore-submodules --",
             "git diff-index --cached --quiet HEAD --ignore-submodules --"].each do |cmd|
                sh cmd do |ok, res|
                    unless ok
                        error "Cannot apply patches because there are uncommitted changes in #{Ansi.purple Dir.pwd}#{Ansi.aqua}\n" +
                              "Unless you have edited the code yourself, these are probably left over from a previous failure.\n" +
                              "You can force the changes to be discarded by running rake with #{Ansi.yellow "-- --force"}#{Ansi.aqua} at the end."
                    end
                end
            end
        end
    end

    def head_commit_message
        `git log -n 1 --format=%s`.chomp
    end

    def generate_patches(from:, dir:, list:)
        dir = relative_path(dir)
        sh "git", "format-patch", "--no-stat", "--no-signature", "-N", "-o", dir, from
        File.open(list, 'w') do |listfile|
            Dir[File.join(dir, "*.patch")].each do |patch|
                patch_dir = File.dirname(patch)
                patch_base = File.basename(patch)
                if patch_base =~ /^\d\d\d\d-(.+)\.patch$/
                    patch_name = $1
                    lines = File.read(patch).lines
                    FileUtils.rm(patch)

                    listfile.puts(patch_name)

                    File.open(File.join(patch_dir, "#{patch_name}.patch"), 'w') do |io|
                        if lines[0] =~ /^From \h+/
                            # Remove the initial "From sha date" line
                            lines = lines[1..-1]
                        end

                        lines.reject do |line|
                            # Remove "index sha..sha mode" lines
                            line =~ /^index \h+\.\.\h+ \d+\s*$/
                        end.each do |line|
                            io.write(line)
                        end
                    end
                else
                    warning "Skipping weird patch file #{patch}"
                end
            end
        end
    end

    def export(from:, to:)
        clean to
        mkdir_p to
        to = File.absolute_path(to)
        Dir.chdir from do
            sh "git archive HEAD | tar -x -C #{to}"
        end
    end

    module Rake
        extend self

        def repo(*args, &block)
            RepoTask.define_task(*args, &block)
        end

        def submodule(*args)
            repo(*args) do |task|
                Git.submodule_update(task.file)
            end
        end

        class RepoTask < ::Rake::FileTask
            def needed?
                !File.exists?(File.join(name, ".git"))
            end
        end
    end
end
