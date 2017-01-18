package org.bukkit;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Common interface for any type of object that can be associated with a specific world.
 * This interface makes no guarantees about the mutability or nullability of the world.
 */
public interface Physical {

    /**
     * Return the {@link UUID} of the {@link World} this object is associated with.
     *
     * Some types may make this value available even when the world is not
     * e.g. because it is not currently loaded.
     *
     * May throw {@link IllegalStateException} if this particular object is
     * not associated with any world.
     */
    default UUID getWorldId() {
        return getWorld().getWorldId();
    }

    /**
     * Return the same {@link UUID} as {@link #getWorldId()}, if it is available,
     * otherwise return null.
     *
     * The default implementation just calls {@link #getWorldId()}, assuming
     * that it never fails. If it can fail, then this method must be
     * overridden to return null in that case.
     */
    default @Nullable UUID tryWorldId() {
        return getWorldId();
    }

    /**
     * Return the {@link World} this object is associated with.
     *
     * May throw an {@link IllegalStateException} if the world is unloaded,
     * or unavailable for some other reason.
     */
    World getWorld();

    /**
     * Return the same {@link World} as {@link #getWorld()}, if it is available,
     * otherwise return null.
     *
     * The default implementation tries to lookup the world by ID through {@link Bukkit#getWorld(UUID)}.
     */
    default @Nullable World tryWorld() {
        final UUID id = tryWorldId();
        return id == null ? null : Bukkit.getWorld(id);
    }
}
