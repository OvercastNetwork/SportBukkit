package org.bukkit.event.player;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a player is about to teleport because it is in contact with a
 * portal.
 * <p>
 * The player will exit the portal at the {@link #getTo()} location, which can
 * be altered by event handlers through {@link #setTo(Location)}. If the portal
 * is not going to teleport the player, then {@link #getTo()} will be the same
 * as {@link #getFrom()}, and the event will be initially set to cancelled.
 * <p>
 * For other entities see {@link org.bukkit.event.entity.EntityPortalEvent}
 */
public class PlayerPortalEvent extends PlayerTeleportEvent {
    private static final HandlerList handlers = new HandlerList();
    protected boolean useTravelAgent = true;
    protected TravelAgent travelAgent;

    public PlayerPortalEvent(final Player player, final Location from, final @Nullable Location to, final TravelAgent pta) {
        this(player, from, to, pta, TeleportCause.UNKNOWN);
    }

    public PlayerPortalEvent(Player player, Location from, @Nullable Location to, TravelAgent pta, TeleportCause cause) {
        super(player, from, to != null ? to : from, cause);
        this.travelAgent = pta;
        setCancelled(to == null);
    }

    /**
     * Sets whether or not the Travel Agent will be used.
     * <p>
     * If this is set to true, the TravelAgent will try to find a Portal at
     * the {@link #getTo()} Location, and will try to create one if there is
     * none.
     * <p>
     * If this is set to false, the {@link #getPlayer()} will only be
     * teleported to the {@link #getTo()} Location.
     *
     * @param useTravelAgent whether to use the Travel Agent
     */
    public void useTravelAgent(boolean useTravelAgent) {
        this.useTravelAgent = useTravelAgent;
    }

    /**
     * Gets whether or not the Travel Agent will be used.
     * <p>
     * If this is set to true, the TravelAgent will try to find a Portal at
     * the {@link #getTo()} Location, and will try to create one if there is
     * none.
     * <p>
     * If this is set to false, the {@link #getPlayer()}} will only be
     * teleported to the {@link #getTo()} Location.
     *
     * @return whether to use the Travel Agent
     */
    public boolean useTravelAgent() {
        return useTravelAgent && travelAgent != null;
    }

    /**
     * Gets the Travel Agent used (or not) in this event.
     *
     * @return the Travel Agent used (or not) in this event
     */
    public TravelAgent getPortalTravelAgent() {
        return this.travelAgent;
    }

    /**
     * Sets the Travel Agent used (or not) in this event.
     *
     * @param travelAgent the Travel Agent used (or not) in this event
     */
    public void setPortalTravelAgent(TravelAgent travelAgent) {
        this.travelAgent = travelAgent;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}