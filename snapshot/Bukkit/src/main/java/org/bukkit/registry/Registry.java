package org.bukkit.registry;

import java.util.NoSuchElementException;
import java.util.Set;

public interface Registry<V extends Registerable> extends Iterable<V> {

    V get(Key key);

    V need(Key key) throws NoSuchElementException;

    boolean containsKey(Key key);

    Set<Key> keySet();

    /**
     * Return an "empty" or "default" value of this registry's type, or null if there is no such value.
     *
     * The exact nature of this value depends on the registry type, but it can generally be used
     * as a fallback where a specific value is unavailable.
     */
    V getFallback();
}
