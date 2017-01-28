package org.bukkit.plugin.java;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.inject.Module;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
final class PluginClassLoader extends URLClassLoader {
    private final JavaPluginLoader pluginLoader;
    private final Set<PluginClassLoader> dependencies;
    private final Set<PluginClassLoader> linearizedDependencies = new LinkedHashSet<PluginClassLoader>();
    private final ReadWriteLock dependencyLock = new ReentrantReadWriteLock();
    private final PluginDescriptionFile description;
    private final File dataFolder;
    private final File file;
    JavaPlugin plugin;

    static {
        registerAsParallelCapable();
    }

    PluginClassLoader(final JavaPluginLoader pluginLoader, final ClassLoader parent, final Set<PluginClassLoader> dependencies, final PluginDescriptionFile description, final File dataFolder, final File file) throws MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);
        Validate.notNull(pluginLoader, "Loader cannot be null");
        Validate.notNull(parent, "Parent loader cannot be null");

        this.pluginLoader = pluginLoader;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;

        this.dependencies = dependencies;
        linearizeDependencies();
    }

    JavaPlugin createPlugin() throws InvalidPluginException {
        Validate.isTrue(plugin == null, "Plugin already created");
        try {
            Class<?> jarClass;
            try {
                jarClass = loadLocalClass(description.getMain(), true);
            } catch (ClassNotFoundException ex) {
                throw new InvalidPluginException("Cannot find main class `" + description.getMain() + "'", ex);
            }

            if(JavaPlugin.class.isAssignableFrom(jarClass)) {
                plugin = jarClass.asSubclass(JavaPlugin.class).newInstance();
            } else if(Module.class.isAssignableFrom(jarClass)) {
                plugin = new ModularPlugin(jarClass.asSubclass(Module.class).newInstance());
            } else {
                throw new InvalidPluginException("main class `" + jarClass.getName() +
                                                 "' must extend either " + JavaPlugin.class.getName() +
                                                 " or " + Module.class.getName());
            }

            return plugin;
        } catch (IllegalAccessException ex) {
            throw new InvalidPluginException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidPluginException("Abnormal plugin type", ex);
        }
    }

    /**
     * Flatten the dependency tree using a depth-first tree walk
     */
    private void linearizeDependencies() {
        linearizedDependencies.clear();
        linearizedDependencies.add(this);
        for(PluginClassLoader dependency : dependencies) {
            linearizedDependencies.addAll(dependency.linearizedDependencies);
        }
    }

    /**
     * Similar to the parent method, except it tries to find the class locally
     * before delegating to the parent loader. The effect is that local classes
     * are always loaded when available, even if a class with the same name is
     * already available from the parent loader. This allows multiple plugins to
     * use different versions of the same class.
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if(description.isIsolated()) {
            // Local, dependencies, Bukkit
            try {
                // Search local plugin, followed by dependencies
                return loadDependencyClass(name, resolve);
            } catch(ClassNotFoundException e) {
                // Search non-plugin classes (
                return loadBukkitClass(name, resolve);
            }
        } else {
            // Bukkit, all plugins in loading order
            try {
                return loadBukkitClass(name, resolve);
            } catch(ClassNotFoundException e) {
                // Search all plugins in load order
                return loadPluginClass(name, resolve);
            }
        }
    }

    /**
     * Load a class from the first dependency that provides it
     */
    Class<?> loadDependencyClass(String name, boolean resolve) throws ClassNotFoundException {
        dependencyLock.readLock().lock();
        try {
            for(PluginClassLoader dependency : linearizedDependencies) {
                try {
                    return dependency.loadLocalClass(name, resolve);
                } catch(ClassNotFoundException ignored) {}
            }
        } finally {
            dependencyLock.readLock().unlock();
        }

        throw new ClassNotFoundException(name);
    }

    /**
     * Load a class from this plugin
     */
    Class<?> loadLocalClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized(getClassLoadingLock(name)) {
            Class<?> cls = findLoadedClass(name);
            if(cls == null) cls = findClass(name);
            if(resolve) resolveClass(cls);
            return cls;
        }
    }

    /**
     * Load a class from the first non-isolated plugin that provides it (including this plugin), in loading order.
     */
    Class<?> loadPluginClass(String name, boolean resolve) throws ClassNotFoundException {
        pluginLoader.loaderLock.readLock().lock();
        try {
            for(PluginClassLoader loader : pluginLoader.loaders.values()) {
                if(!loader.description.isIsolated()) {
                    try {
                        return loader.loadLocalClass(name, resolve);
                    } catch(ClassNotFoundException ignored) {}
                }
            }
        } finally {
            pluginLoader.loaderLock.readLock().unlock();
        }

        throw new ClassNotFoundException(name);
    }

    Class<?> loadBukkitClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> cls = getParent().loadClass(name);
        if(resolve) resolveClass(cls);
        return cls;
    }

    void removeDependency(PluginClassLoader dependency) {
        dependencyLock.writeLock().lock();
        try {
            dependencies.remove(dependency);
            linearizeDependencies();
        } finally {
            dependencyLock.writeLock().unlock();
        }
    }

    synchronized void initialize(JavaPlugin javaPlugin) {
        Validate.notNull(javaPlugin, "Initializing plugin cannot be null");

        if (this.plugin != null) {
            throw new IllegalArgumentException("Plugin already initialized!");
        }

        javaPlugin.init(pluginLoader, pluginLoader.server, description, dataFolder, file, this);
    }
}
