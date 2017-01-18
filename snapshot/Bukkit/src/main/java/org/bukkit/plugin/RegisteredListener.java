package org.bukkit.plugin;

import javax.annotation.Nullable;

import org.bukkit.event.*;
import org.bukkit.event.Listener;

/**
 * A {@link BoundEventHandler} belonging to a particular {@link Plugin}.
 *
 * @deprecated legacy compatibility
 */
@Deprecated
public class RegisteredListener extends BoundEventHandler {

    private final Plugin plugin;
    final @Nullable PluginEventRegistry registry;

    // This constructor is for upstream compatibility
    public RegisteredListener(final Listener listener, final EventExecutor executor, final EventPriority priority, final Plugin plugin, final boolean ignoreCancelled) {
        super(new EventHandlerMeta<>(Event.class, priority, ignoreCancelled), new EventExecutorAdapter<>(executor), listener);
        this.plugin = plugin;
        this.registry = null;
    }

    RegisteredListener(EventHandlerMeta meta, org.bukkit.event.EventExecutor executor, tc.oc.minecraft.api.event.Listener listener, Plugin plugin, PluginEventRegistry registry) {
        super(meta, executor, listener);
        this.plugin = plugin;
        this.registry = registry;
    }

    /**
     * Gets the plugin for this registration
     *
     * @return Registered Plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean isEnabled() {
        return getPlugin().isEnabled();
    }

    // These aliases provided for legacy binary compatibility
    public Listener getListener() { return (Listener) listener(); }
    public EventPriority getPriority() { return meta().priority(); }
    public boolean isIgnoringCancelled() { return meta().ignoreCancelled(); }
}
