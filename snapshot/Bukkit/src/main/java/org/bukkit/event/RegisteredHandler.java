package org.bukkit.event;

import javax.annotation.Nullable;

/**
 * An event handler, i.e. an {@link EventCallable} that intercepts {@link Event}s.
 */
public interface RegisteredHandler<T extends Event> extends EventCallable<T> {

    EventHandlerMeta<T> meta();

    @Override
    void callEvent(T event) throws EventException;

    default boolean isEnabled() {
        return true;
    }

    default boolean canHandle(Event event, @Nullable EventPriority priority) {
        return isEnabled() && meta().canHandle(event, priority);
    }
}
