package org.bukkit.event.entity;

import org.bukkit.World;
import org.bukkit.Physical;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;

/**
 * Represents an Entity-related event
 */
public abstract class EntityEvent extends Event implements Physical {
    protected Entity entity;

    public EntityEvent(final Entity what) {
        entity = what;
    }

    /**
     * Returns the Entity involved in this event
     *
     * @return Entity who is involved in this event
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the EntityType of the Entity involved in this event.
     *
     * @return EntityType of the Entity involved in this event
     */
    public EntityType getEntityType() {
        return entity.getType();
    }

    @Override
    public World getWorld() {
        return getEntity().getWorld();
    }
}
