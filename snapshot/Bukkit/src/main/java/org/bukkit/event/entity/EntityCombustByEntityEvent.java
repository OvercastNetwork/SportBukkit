package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.EntityAction;

/**
 * Called when an entity causes another entity to combust.
 */
public class EntityCombustByEntityEvent extends EntityCombustEvent implements EntityAction {
    private final Entity combuster;

    public EntityCombustByEntityEvent(final Entity combuster, final Entity combustee, final int duration) {
        super(combustee, duration);
        this.combuster = combuster;
    }

    /**
     * Get the entity that caused the combustion event.
     *
     * @return the Entity that set the combustee alight.
     */
    public Entity getCombuster() {
        return combuster;
    }

    @Override
    public Entity getActor() {
        return getCombuster();
    }
}
