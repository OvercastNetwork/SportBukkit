package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

/**
 * This event is called when an {@link org.bukkit.entity.Entity} is removed from the world because it has fallen 64
 * blocks into the void.
 */
public class EntityDespawnInVoidEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public EntityDespawnInVoidEvent(Entity what) {
        super(what);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
