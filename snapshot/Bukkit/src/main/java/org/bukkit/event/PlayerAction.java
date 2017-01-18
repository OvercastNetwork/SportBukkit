package org.bukkit.event;

import org.bukkit.entity.Player;

/**
 * Implemented by {@link Event}s which may represent the action of a {@link Player}
 */
public interface PlayerAction extends EntityAction {
    /**
     * @return the player that performed this action, or null if the event was not caused
     *         by a player, or the causing player is unavailable for some reason.
     */
    Player getActor();
}
