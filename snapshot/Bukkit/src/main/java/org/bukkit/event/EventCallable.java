package org.bukkit.event;

/**
 * Something that is called with an {@link Event}, has several uses.
 */
public interface EventCallable<T extends Event> {

    void callEvent(T event) throws Throwable;

    EventCallable<?> EMPTY = event -> {};
}
