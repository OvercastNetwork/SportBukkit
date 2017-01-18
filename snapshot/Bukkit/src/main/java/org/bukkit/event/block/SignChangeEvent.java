package org.bukkit.event.block;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.PlayerAction;

/**
 * Called when a sign is changed by a player.
 * <p>
 * If a Sign Change event is cancelled, the sign will not be changed.
 */
public class SignChangeEvent extends BlockEvent implements Cancellable, PlayerAction {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Player player;
    private final BaseComponent[] lines;

    public SignChangeEvent(final Block theBlock, final Player thePlayer, final BaseComponent[] theLines) {
        super(theBlock);
        this.player = thePlayer;
        this.lines = theLines;
    }

    /**
     * Gets the player changing the sign involved in this event.
     *
     * @return the Player involved in this event
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public Player getActor() {
        return getPlayer();
    }

    /**
     * Gets all of the lines of text from the sign involved in this event.
     *
     * @return the BaseComponent array for the sign's lines new text
     */
    public BaseComponent[] lines() {
        return lines;
    }

    @Deprecated
    public String[] getLines() {
        return BaseComponent.toLegacyArray(lines);
    }

    /**
     * Gets a single line of text from the sign involved in this event.
     *
     * @param index index of the line to get
     * @return the BaseComponent containing the line of text associated with the
     *     provided index
     * @throws IndexOutOfBoundsException thrown when the provided index is {@literal > 3
     *     or < 0}
     */
    public BaseComponent line(int index) {
        return lines[index];
    }

    @Deprecated
    public String getLine(int index) throws IndexOutOfBoundsException {
        return lines[index].toLegacyText();
    }

    /**
     * Sets a single line for the sign involved in this event
     *
     * @param index index of the line to set
     * @param line text to set
     * @throws IndexOutOfBoundsException thrown when the provided index is {@literal > 3
     *     or < 0}
     */
    public void setLine(int index, BaseComponent line) {
        lines[index] = line;
    }

    @Deprecated
    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        lines[index] = TextComponent.fromLegacyToComponent(line, false);
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
