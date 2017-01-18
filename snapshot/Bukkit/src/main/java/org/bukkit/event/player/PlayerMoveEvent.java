package org.bukkit.event.player;

import com.google.common.base.Preconditions;
import org.bukkit.EntityLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds information for player movement events
 */
public class PlayerMoveEvent extends PlayerActionBase implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private EntityLocation from;
    private EntityLocation to;

    public PlayerMoveEvent(final Player player, final EntityLocation from, final EntityLocation to) {
        super(player);
        this.from = checkNotNull(from);
        this.to = checkNotNull(to);
    }

    public PlayerMoveEvent(final Player player, final Location from, final Location to) {
        this(
            player,
            EntityLocation.coerce(from, player.getEntityLocation()),
            EntityLocation.coerce(to, player.getEntityLocation())
        );
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     * <p>
     * If a move or teleport event is cancelled, the player will be moved or
     * teleported back to the Location as defined by getFrom(). This will not
     * fire an event
     *
     * @return true if this event is cancelled
     */
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     * <p>
     * If a move or teleport event is cancelled, the player will be moved or
     * teleported back to the Location as defined by getFrom(). This will not
     * fire an event
     *
     * @param cancel true if you wish to cancel this event
     */
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets the location this player moved from
     *
     * @return Location the player moved from
     */
    public Location getFrom() {
        return from;
    }

    public EntityLocation getEntityFrom() {
        return from;
    }

    /**
     * Sets the location to mark as where the player moved from
     *
     * @param from New location to mark as the players previous location
     */
    public void setFrom(Location from) {
        validateLocation(from);
        this.from = EntityLocation.coerce(from, this.from);
    }

    /**
     * Gets the location this player moved to
     *
     * @return Location the player moved to
     */
    public Location getTo() {
        return to;
    }

    public EntityLocation getEntityTo() {
        return to;
    }

    /**
     * Sets the location that this player will move to
     *
     * @param to New Location this player will move to
     */
    public void setTo(Location to) {
        validateLocation(to);
        this.to = EntityLocation.coerce(to, this.to);
    }

    private void validateLocation(Location loc) {
        Preconditions.checkArgument(loc != null, "Cannot use null location!");
        Preconditions.checkArgument(loc.getWorld() != null, "Cannot use null location with null world!");
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
