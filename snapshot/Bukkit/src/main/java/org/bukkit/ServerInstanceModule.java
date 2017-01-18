package org.bukkit;

import java.util.Collection;

import com.google.inject.AbstractModule;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginInstanceModule;
import tc.oc.inject.KeyedModule;
import tc.oc.inject.ProtectedBinder;

/**
 * Configures a {@link Server} instance and a collection of {@link Plugin}s.
 */
public class ServerInstanceModule extends KeyedModule {

    private final Server server;
    private final Collection<Plugin> plugins;

    public ServerInstanceModule(Server server, Collection<Plugin> plugins) {
        super(server);
        this.server = server;
        this.plugins = plugins;
    }

    @Override
    protected void configure() {
        install(new ServerModule());
        bind(Server.class).toInstance(server);

        for(Plugin plugin : plugins) {
            ProtectedBinder.newProtectedBinder(binder())
                           .install(new PluginInstanceModule(plugin));
        }
    }
}
