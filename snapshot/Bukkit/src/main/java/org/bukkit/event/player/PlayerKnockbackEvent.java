package org.bukkit.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Called when a player is knocked back by an attack or injury.
 */
public class PlayerKnockbackEvent extends PlayerVelocityEvent {

    private final Entity damager;

    public PlayerKnockbackEvent(Player player, Entity damager, Vector velocity, boolean impulse) {
        super(player, velocity, impulse);
        this.damager = damager;
    }

    /**
     * The entity that is knocking the player back, or null if the knockback
     * is not caused by an entity.
     */
    public Entity getDamager() {
        return damager;
    }
}
