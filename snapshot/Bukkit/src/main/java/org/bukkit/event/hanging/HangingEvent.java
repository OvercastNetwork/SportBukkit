package org.bukkit.event.hanging;

import org.bukkit.entity.Hanging;
import org.bukkit.event.entity.EntityEvent;

/**
 * Represents a hanging entity-related event.
 */
public abstract class HangingEvent extends EntityEvent {
    protected Hanging hanging;

    protected HangingEvent(final Hanging painting) {
        super(painting);
        this.hanging = painting;
    }

    /**
     * Gets the hanging entity involved in this event.
     *
     * @return the hanging entity
     */
    public Hanging getEntity() {
        return hanging;
    }
}
