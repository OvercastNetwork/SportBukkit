package org.bukkit.plugin;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

/**
 * An event handler that belongs to a {@link Listener}, and must be passed an
 * instance of the listener when handling an event.
 */
public interface EventExecutor {
    void execute(Listener listener, Event event) throws EventException;
}
