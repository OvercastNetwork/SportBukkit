package org.bukkit.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nullable;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import tc.oc.exception.ExceptionHandler;
import org.bukkit.generator.ChunkGenerator;

public class TestPlugin extends PluginBase {
    private boolean enabled = true;
    private boolean naggable = true;

    final private String pluginName;
    final private @Nullable Server server;
    final private Logger logger;
    final private ExceptionHandler exceptionHandler;
    final private PluginEventRegistry eventRegistry;

    public TestPlugin(String pluginName) {
        this(pluginName, null);
    }

    public TestPlugin(String pluginName, @Nullable Server server) {
        this.pluginName = pluginName;
        this.server = server;
        this.logger = server != null ? new PluginLogger(this) : Logger.getGlobal();
        this.exceptionHandler = new PluginExceptionHandler(this);
        this.eventRegistry = new PluginEventRegistry(this);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public File getDataFolder() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public PluginDescriptionFile getDescription() {
        return new PluginDescriptionFile(pluginName, "1.0", "test.test");
    }

    public FileConfiguration getConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public InputStream getResource(String filename) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void saveConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void saveDefaultConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void saveResource(String resourcePath, boolean replace) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void reloadConfig() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public ExceptionHandler exceptionHandler() {
        return exceptionHandler;
    }

    public PluginLoader getPluginLoader() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public PluginEventRegistry eventRegistry() {
        return eventRegistry;
    }

    public Server getServer() {
        if(server != null) return server;
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void onDisable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void onLoad() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void onEnable() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean isNaggable() {
        return naggable;
    }

    public void setNaggable(boolean naggable) {
        this.naggable = naggable;
    }

    public EbeanServer getDatabase() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
