package org.bukkit.command;

import org.bukkit.Server;
import org.bukkit.permissions.Permissible;

public interface CommandSender extends Permissible, tc.oc.minecraft.api.command.CommandSender {

    /**
     * Sends this sender a message
     *
     * @param message Message to be displayed
     */
    public void sendMessage(String message);

    /**
     * Sends this sender multiple messages
     *
     * @param messages An array of messages to be displayed
     */
    public void sendMessage(String[] messages);

    /**
     * Returns the server instance that this command is running on
     *
     * @return Server instance
     */
    public Server getServer();

    /**
     * Gets the name of this command sender
     *
     * @return Name of the sender
     */
    public String getName();

    /**
     * Return this sender's name as viewed by the given sender. Used by
     * {@link org.bukkit.entity.Player}s to support fake names.
     */
    public String getName(CommandSender viewer);

    @Override
    default String getName(tc.oc.minecraft.api.command.CommandSender viewer) {
        return getName((CommandSender) viewer);
    }
}
