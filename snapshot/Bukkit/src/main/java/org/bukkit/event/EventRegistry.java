package org.bukkit.event;

import java.lang.reflect.Method;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import tc.oc.exception.ExceptionHandler;
import tc.oc.minecraft.api.event.Listener;

/**
 * Service used to reflectively create and register {@link RegisteredHandler}s for {@link Listener}s.
 *
 * Handlers created through an {@link EventRegistry} retain a reference to that registry,
 * allowing them to be unregistered all at once.
 *
 * An {@link EventRegistry} has an implicit {@link ExceptionHandler} that it uses to
 * construct handlers, though this is not exposed in the base API.
 */
public interface EventRegistry extends tc.oc.minecraft.api.event.EventRegistry {

    /**
     * Create a {@link EventMethodExecutor} wrapping the given method.
     *
     * The method is not checked for annotations. The handler parameters
     * are derived exclusively from the given {@link EventHandlerMeta}.
     *
     * @throws IllegalArgumentException if the method signature is not
     *         compatible with the event parameters.
     */
    <T extends Event> EventMethodExecutor<T> createHandler(EventHandlerMeta<T> meta, Method method);

    /**
     * Reflect on the given {@link Method} and create a {@link EventMethodExecutor} for it,
     * using the method's signature and annotations.
     *
     * If the method is not annotated as an {@link EventHandler}, return null.
     *
     * @throws IllegalArgumentException if the method signature is not
     *         compatible with the event parameters.
     */
    @Nullable EventMethodExecutor<?> createHandler(Method method);

    /**
     * Create a {@link BoundEventHandler} binding the given {@link EventExecutor} to the given {@link Listener}.
     *
     * This only creates the handler, it does not register it to receive any events.
     *
     * TODO: The executor could be created from a different registry.
     * It's hard to prevent this while still supporting the old API.
     */
    <T extends Event> BoundEventHandler<T> bindHandler(EventHandlerMeta<T> meta, Listener listener, EventExecutor<T> executor);

    /**
     * Create a {@link BoundEventHandler} binding the given {@link EventMethodExecutor} to the given {@link Listener}.
     *
     * This only creates the handler, it does not register it to receive any events.
     *
     * @throws IllegalArgumentException if the method does not belong to the listener
     *
     * TODO: The executor could be created from a different registry.
     * It's hard to prevent this while still supporting the old API.
     */
    <T extends Event> BoundEventHandler<T> bindHandler(Listener listener, EventMethodExecutor<T> executor);

    /**
     * Create {@link EventMethodExecutor}s for all event handler methods found in the given {@link Listener} class.
     *
     * This includes any methods inherited from superclasses or interfaces.
     */
    Stream<EventMethodExecutor<?>> createHandlers(Class<? extends Listener> listener);

    /**
     * Create {@link BoundEventHandler}s for all event handler methods found in the given {@link Listener}.
     *
     * This includes any methods inherited from superclasses or interfaces.
     *
     * @see #createHandlers
     */
    Stream<BoundEventHandler<?>> bindHandlers(Listener listener);
}
