package org.bukkit.plugin;

import javax.inject.Inject;

import tc.oc.minecraft.api.event.Listener;
import org.bukkit.event.Event;
import org.bukkit.event.EventExecutor;
import org.bukkit.event.EventHandlerMeta;
import org.bukkit.event.EventMethodExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.SimpleEventRegistry;

public class PluginEventRegistry extends SimpleEventRegistry {

    private final Plugin plugin;

    @Inject public PluginEventRegistry(Plugin plugin) {
        super(plugin.exceptionHandler());
        this.plugin = plugin;
    }

    @Override
    public void registerListener(Listener listener) {
        if(!plugin.isEnabled()) {
            throw new IllegalPluginAccessException(
                "Plugin " + plugin.getDescription().getFullName() +
                " attempted to register event listener while not enabled"
            );
        }
        super.registerListener(listener);
    }

    @Override
    public <T extends Event> RegisteredListener bindHandler(EventHandlerMeta<T> meta, Listener listener, EventExecutor<T> executor) {
        return plugin.getServer().pluginProfiling()
               ? new TimedRegisteredListener(meta, executor, listener, plugin, this)
               : new RegisteredListener(meta, executor, listener, plugin, this);
    }

    @Override
    public <T extends Event> RegisteredListener bindHandler(Listener listener, EventMethodExecutor<T> executor) {
        return bindHandler(executor.meta(), listener, executor);
    }

    @Override
    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(handler -> handler instanceof RegisteredListener &&
                                             this == ((RegisteredListener) handler).registry &&
                                             listener == ((RegisteredListener) handler).listener());
    }

    @Override
    public void unregisterAll() {
        HandlerList.unregisterAll(handler -> handler instanceof RegisteredListener &&
                                             this == ((RegisteredListener) handler).registry);
    }
}
