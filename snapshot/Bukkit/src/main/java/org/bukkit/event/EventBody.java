package org.bukkit.event;

/**
 * Encapsulates the primary functionality of an event.
 *
 * @see EventBus#callEvent
 */
public interface EventBody<T extends Event, X extends Throwable> extends EventCallable<T> {
    @Override
    void callEvent(T event) throws X;
}
