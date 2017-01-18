package org.bukkit.craftbukkit.registry;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterators;
import net.minecraft.server.MinecraftKey;
import net.minecraft.server.RegistryMaterials;
import org.bukkit.registry.Key;
import org.bukkit.registry.Registerable;
import org.bukkit.registry.Registry;

public abstract class CraftRegistry<V extends Registerable, M> implements Registry<V> {

    private final Class<V> type;
    private final RegistryMaterials<MinecraftKey, M> handle;

    private final LoadingCache<M, V> bukkitCache = CacheBuilder.newBuilder().build(new CacheLoader<M, V>() {
        @Override
        public V load(M nms) throws Exception {
            return createBukkit(nms);
        }
    });

    public CraftRegistry(Class<V> type, RegistryMaterials<MinecraftKey, M> handle) {
        this.type = type;
        this.handle = handle;
    }

    protected abstract V createBukkit(M nms);

    public final V toBukkit(M nms) {
        return bukkitCache.getUnchecked(nms);
    }

    protected RegistryMaterials<MinecraftKey, M> handle() {
        return handle;
    }

    @Override
    public V need(Key key) throws NoSuchElementException {
        final V value = get(key);
        if(value == null) {
            throw new NoSuchElementException("No " + type.getName() + " with key " + key);
        }
        return value;
    }

    @Override
    public V get(Key key) {
        final M nms = handle.get(CraftKey.toNms(key));
        return nms == null ? null : bukkitCache.getUnchecked(nms);
    }

    @Override
    public boolean containsKey(Key key) {
        return handle.d(CraftKey.toNms(key));
    }

    @Override
    public Set<Key> keySet() {
        return new AbstractSet<Key>() {
            @Override
            public boolean contains(Object o) {
                return o instanceof Key &&
                       handle.keySet().contains(CraftKey.toNms((Key) o));
            }

            @Override
            public Iterator<Key> iterator() {
                return Iterators.transform(handle.keySet().iterator(), new Function<MinecraftKey, Key>() {
                    @Override
                    public Key apply(MinecraftKey nms) {
                        return CraftKey.get(nms);
                    }
                });
            }

            @Override
            public int size() {
                return handle.keySet().size();
            }
        };
    }

    @Override
    public Iterator<V> iterator() {
        return Iterators.transform(handle.iterator(), new Function<M, V>() {
            @Override
            public V apply(M nms) {
                return createBukkit(nms);
            }
        });
    }
}
