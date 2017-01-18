package org.bukkit.craftbukkit.registry;

import net.minecraft.server.MinecraftKey;
import org.bukkit.registry.Key;

public class CraftKey implements Key {

    private final MinecraftKey handle;

    private CraftKey(MinecraftKey handle) {
        this.handle = handle;
    }

    @Override
    public String prefix() {
        return handle.b();
    }

    @Override
    public String id() {
        return handle.a();
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (
            o instanceof Key &&
            prefix().equals(((Key) o).prefix()) &&
            id().equals(((Key) o).id())
        );
    }

    @Override
    public String toString() {
        return handle.toString();
    }

    public static MinecraftKey toNms(Key key) {
        if(key instanceof CraftKey) {
            return ((CraftKey) key).handle;
        } else {
            return new MinecraftKey(key.prefix(), key.id());
        }
    }

    public static Key get(MinecraftKey nms) {
        return new CraftKey(nms);
    }

    public static Key get(String prefix, String id) {
        return get(new MinecraftKey(prefix, id));
    }

    public static Key get(String key) {
        return get(new MinecraftKey(key));
    }
}
