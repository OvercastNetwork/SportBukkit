package org.bukkit.event.player;

import org.bukkit.World;
import org.bukkit.Physical;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Represents a player related event
 */
public abstract class PlayerEvent extends Event implements Physical {
    protected Player player;

    public PlayerEvent(final Player who) {
        player = who;
    }

    PlayerEvent(final Player who, boolean async) {
        super(async);
        player = who;

    }

    /**
     * Returns the player involved in this event
     *
     * @return Player who is involved in this event
     */
    public final Player getPlayer() {
        return player;
    }

    @Override
    public World getWorld() {
        return getPlayer().getWorld();
    }
}
