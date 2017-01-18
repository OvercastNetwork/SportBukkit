package org.bukkit.event;

import javax.annotation.Nullable;

import tc.oc.exception.ExceptionHandler;

public interface EventBus {

    void callEvent(Event event) throws IllegalStateException;

    void callEvent(Event event, @Nullable EventPriority priority);

    <T extends Event, X extends Throwable> void callEvent(T event, @Nullable EventBody<? super T, X> body) throws X;

    /**
     * Dispatch the given event to all applicable {@link RegisteredHandler}s,
     * then (optionally) to the given {@link EventBody}.
     *
     * Event handlers are usually created from an {@link EventRegistry}, and registered with a {@link HandlerList}.
     *
     * Event handlers are called in order of their {@link EventPriority}, from lowest to highest,
     * before the body of the event is executed. If an event handler calls {@link Event#yield()},
     * the following handlers, and the event body, will run before it returns. If a handler does
     * not yield, dispatch of the event will continue after the handler returns.
     *
     * Exceptions thrown from event handlers are reported to an {@link ExceptionHandler}.
     * The exception does NOT prevent other handlers, or the event body, from running.
     *
     * Exceptions thrown from the event body are propagated out of this method
     * (provided that yielding handlers propagate the {@link EventException} wrapping it).
     *
     * @param event         Event object passed to handlers
     * @param priority      If non-null, only call handlers at this priority level
     * @param body          Body of the actual event, or null for a no-op event
     *
     * @throws IllegalStateException Thrown when an asynchronous event is
     *     fired from synchronous code.
     *     <p>
     *     <i>Note: This is best-effort basis, and should not be used to test
     *     synchronized state. This is an indicator for flawed flow logic.</i>
     */
    <T extends Event, X extends Throwable> void callEvent(T event, @Nullable EventPriority priority, @Nullable EventBody<? super T, X> body) throws X;

    /**
     * Dispatch the given event to a single {@link RegisteredHandler}.
     *
     * If the handler yields, the given body is called (if it's non-null), and this method returns true.
     * If the handler does not yield, the body is NOT called, and this method returns false.
     * If the handler returns false from {@link RegisteredHandler#canHandle}, then it is not called, and the method returns false.
     *
     * Exceptions thrown from the event body are propagated out of this method
     * (provided that yielding handlers propagate the {@link EventException} wrapping it).
     */
    <T extends Event, X extends Throwable> boolean callEventHandler(T event, @Nullable EventPriority priority, RegisteredHandler<? super T> handler, @Nullable EventBody<? super T, X> body) throws X;
}
