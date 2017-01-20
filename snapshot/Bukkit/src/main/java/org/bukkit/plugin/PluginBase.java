package org.bukkit.plugin;

import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;

import com.google.inject.Injector;
import org.bukkit.event.EventRegistry;
import org.bukkit.permissions.Permission;
import tc.oc.minecraft.api.event.ListenerContext;
import tc.oc.exception.ExceptionHandler;
import tc.oc.minecraft.api.text.TextRenderer;

/**
 * Represents a base {@link Plugin}
 * <p>
 * Extend this class if your plugin is not a {@link
 * org.bukkit.plugin.java.JavaPlugin}
 */
public abstract class PluginBase implements Plugin {

    @Inject private Injector injector;
    @Inject private PluginManager pluginManager;
    @Inject private ExceptionHandler exceptionHandler;
    @Inject private EventRegistry eventRegistry;

    @Inject private Set<Permission> permissions;
    @Inject private Provider<ListenerContext> listenerContext;
    @Inject private Provider<Collection<Provider<TextRenderer>>> textRenderers;

    protected void assertInjected() {
        if(injector == null) {
            throw new IllegalStateException("Not available until plugin has been injected");
        }
    }

    @Override
    public Injector injector() {
        assertInjected();
        return injector;
    }

    @Override
    public EventRegistry eventRegistry() {
        assertInjected();
        return eventRegistry;
    }

    @Override
    public ExceptionHandler exceptionHandler() {
        assertInjected();
        return exceptionHandler;
    }

    @Override
    public Collection<Provider<TextRenderer>> textRenderers() {
        assertInjected();
        return textRenderers.get();
    }

    protected final void preEnable() {
        permissions.forEach(pluginManager::addPermission);
        listenerContext.get().enable();
    }

    protected final void postDisable() {
        listenerContext.get().disable();
        permissions.forEach(pluginManager::removePermission);
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Plugin)) {
            return false;
        }
        return getName().equals(((Plugin) obj).getName());
    }

    public final String getName() {
        return getDescription().getName();
    }
}
