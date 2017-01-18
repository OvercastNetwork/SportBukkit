package org.bukkit.plugin;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The PluginLogger class is a modified {@link Logger} that prepends all
 * logging calls with the name of the plugin doing the logging. The API for
 * PluginLogger is exactly the same as {@link Logger}.
 *
 * @see Logger
 */
public class PluginLogger extends Logger {
    private String pluginName;

    public static PluginLogger get(Plugin context) {
        LogManager lm = LogManager.getLogManager();
        Logger logger = lm.getLogger(context.getClass().getCanonicalName());

        if(logger instanceof PluginLogger) {
            return (PluginLogger) logger;
        } else {
            PluginLogger pluginLogger = new PluginLogger(context);

            // Register the logger under the plugin's name, unless some other logger is already using the name
            if(logger == null) {
                lm.addLogger(pluginLogger);
                pluginLogger.setParent(context.getServer().getLogger()); // addLogger changes this, change it back
            }

            return pluginLogger;
        }
    }

    /**
     * Creates a new PluginLogger that extracts the name from a plugin.
     *
     * @param context A reference to the plugin
     */
    public PluginLogger(Plugin context) {
        super(context.getDescription().getMain(), null);
        String prefix = context.getDescription().getPrefix();
        pluginName = prefix != null ? new StringBuilder().append("[").append(prefix).append("] ").toString() : "[" + context.getDescription().getName() + "] ";
        setParent(context.getServer().getLogger());
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(pluginName + logRecord.getMessage());
        super.log(logRecord);
    }

}
