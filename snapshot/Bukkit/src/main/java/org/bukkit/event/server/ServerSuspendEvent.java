package org.bukkit.event.server;

import org.bukkit.Server;
import org.bukkit.event.HandlerList;

/**
 * Fired when the server transitions to a suspended state, which can happen from an
 * explicit call to {@link org.bukkit.Server#setSuspended(boolean)}, or automatically
 * due to {@link Server#getEmptyServerSuspendDelay()}.
 *
 * The suspension happens entirely within the event. A call to {@link #yield()}
 * will not return until the server has resumed.
 */
public class ServerSuspendEvent extends ServerEvent {
    private static final HandlerList<ServerSuspendEvent> handlers = new HandlerList<>();
}
