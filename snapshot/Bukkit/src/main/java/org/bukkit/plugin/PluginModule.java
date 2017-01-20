package org.bukkit.plugin;

import javax.inject.Singleton;

import com.google.inject.Provides;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventRegistry;
import org.bukkit.permissions.PermissionBinder;
import tc.oc.inject.ProtectedModule;
import tc.oc.minecraft.api.event.ListenerBinder;
import tc.oc.exception.ExceptionHandler;
import tc.oc.minecraft.api.plugin.PluginDescription;
import tc.oc.minecraft.api.scheduler.Scheduler;
import tc.oc.minecraft.api.text.TextRendererBinder;

/**
 * Bindings for things belonging to a particular {@link Plugin}.
 *
 * Does not bind {@link Plugin} itself
 *
 * @see PluginInstanceModule
 */
public class PluginModule extends ProtectedModule {

    @Override
    protected void configure() {
        // Ensure these collections have bindings
        new PermissionBinder(binder());
        new ListenerBinder(binder());
        new TextRendererBinder(binder());

        bind(tc.oc.minecraft.api.plugin.Plugin.class).to(Plugin.class);
        bind(PluginDescription.class).to(PluginDescriptionFile.class);
        bind(tc.oc.minecraft.api.configuration.Configuration.class).to(Configuration.class);
        bind(tc.oc.minecraft.api.event.EventRegistry.class).to(EventRegistry.class);

        bind(ExceptionHandler.class).to(PluginExceptionHandler.class).in(Singleton.class);
        bind(EventRegistry.class).to(PluginEventRegistry.class).in(Singleton.class);
        bind(Scheduler.class).to(PluginScheduler.class).in(Singleton.class);
    }

    @Provides
    PluginDescriptionFile description(Plugin plugin) {
        return plugin.getDescription();
    }

    @Provides
    PluginLogger logger(Plugin plugin) {
        return (PluginLogger) plugin.getLogger();
    }

    @Provides
    Configuration configuration(Plugin plugin) {
        return plugin.getConfig();
    }
}
