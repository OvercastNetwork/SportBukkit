package org.bukkit.event;

import org.bukkit.entity.Entity;

/**
 * Implemented by {@link Event}s which can be caused by an {@link Entity}.
 *
 * There is no formal definition of "caused" in this case, only an intuitive one.
 * Events that involve an entity doing something to another entity, or some other
 * object, will typically implement this interface. Events involving only a single
 * entity may or may not implement it, depending on whether the event feels like
 * an "action" by the entity.
 *
 * Examples of the types of events that DO implement this interface include:
 * movements, attacks, item pickup/drop, block place/break, "using" items/entities/blocks,
 * inventory actions, and collisions (with the obstructing entity as the actor).
 *
 * Examples of event types that DO NOT implement this interface include:
 * spawning, despawning, environmental damage/death, natural depletion/recovery of vitals,
 * and events with no direct in-game effect, such as chatting and running commands.
 */
public interface EntityAction {
    /**
     * @return the entity that performed this action, or null if the event was not caused
     *         by an entity, or the causing entity is unavailable for some reason.
     */
    Entity getActor();
}
