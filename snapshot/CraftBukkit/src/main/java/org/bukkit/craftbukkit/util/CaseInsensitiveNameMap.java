package org.bukkit.craftbukkit.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

public class CaseInsensitiveNameMap<V> extends AbstractMap<String, V> {

    private final Collection<V> values;
    private final Function<V, String> nameGetter;
    private final Set<Entry<String, V>> entries;

    public CaseInsensitiveNameMap(Collection<V> values, Function<V, String> nameGetter) {
        this.values = values;
        this.nameGetter = nameGetter;

        this.entries = new AbstractSet<Entry<String, V>>() {
            @Override
            public Iterator<Entry<String, V>> iterator() {
                return Iterators.transform(
                    values.iterator(),
                    value -> Maps.immutableEntry(nameGetter.apply(value), value)
                );
            }

            @Override
            public int size() {
                return values.size();
            }
        };
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return entries;
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return values.contains(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public V get(Object key) {
        if(!(key instanceof String)) return null;
        final String name = (String) key;
        for(V value : values) {
            if(name.equalsIgnoreCase(nameGetter.apply(value))) return value;
        }
        return null;
    }
}
