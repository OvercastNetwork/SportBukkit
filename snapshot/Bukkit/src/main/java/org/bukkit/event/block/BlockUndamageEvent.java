package org.bukkit.event.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.PlayerAction;

/**
 * Called when a player stops digging a block WITHOUT breaking it. This is the
 * counterpart to {@link BlockDamageEvent}, which fires when the player starts
 * digging. This event will not be called if the player completes the dig and
 * breaks the block. It will also not be called if the block breaks instantly
 * when the player starts digging.
 */
public class BlockUndamageEvent extends BlockEvent implements PlayerAction {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public BlockUndamageEvent(final Player player, final Block block) {
        super(block);
        this.player = player;
    }

    /**
     * Gets the player damaging the block involved in this event.
     *
     * @return The player damaging the block involved in this event
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public Player getActor() {
        return getPlayer();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
