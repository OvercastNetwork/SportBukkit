package org.bukkit.event.player;

import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerPickupExperienceEvent extends PlayerActionBase implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ExperienceOrb experienceorb;
    private boolean cancel = false;

    public PlayerPickupExperienceEvent(final Player player, final ExperienceOrb experienceorb) {
        super(player);
        this.experienceorb = experienceorb;
    }

    public ExperienceOrb getExperienceOrb() {
        return experienceorb;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
