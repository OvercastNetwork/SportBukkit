package org.bukkit.plugin;

import java.util.logging.Level;
import javax.inject.Inject;

import tc.oc.exception.LoggingExceptionHandler;

public class PluginExceptionHandler extends LoggingExceptionHandler {

    private final Plugin plugin;

    @Inject public PluginExceptionHandler(Plugin plugin) {
        super(plugin.getLogger());
        this.plugin = plugin;
    }

    @Override
    public void handleException(Throwable exception, String message) {
        if(exception instanceof AuthorNagException) {
            if (plugin.isNaggable()) {
                plugin.setNaggable(false);

                plugin.getLogger().log(Level.SEVERE, String.format(
                    "Nag author(s): '%s' of '%s' about the following: %s",
                    plugin.getDescription().getAuthors(),
                    plugin.getDescription().getFullName(),
                    exception.getMessage()
                ));
            }
        }
        super.handleException(exception, message);
    }
}
