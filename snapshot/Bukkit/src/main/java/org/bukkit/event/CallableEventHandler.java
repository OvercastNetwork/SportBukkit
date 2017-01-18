package org.bukkit.event;

import tc.oc.exception.ExceptionHandler;
import tc.oc.exception.LoggingExceptionHandler;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A general {@link RegisteredHandler} that passes events to a {@link EventCallable},
 * and reports uncaught exceptions to a {@link ExceptionHandler}.
 */
public class CallableEventHandler<T extends Event> implements RegisteredHandler<T> {

    private final EventHandlerMeta<T> meta;
    private final EventCallable<? super T> callable;
    private final ExceptionHandler exceptionHandler;

    public CallableEventHandler(EventHandlerMeta<T> meta, EventCallable<? super T> callable, ExceptionHandler exceptionHandler) {
        this.meta = checkNotNull(meta);
        this.callable = checkNotNull(callable);
        this.exceptionHandler = checkNotNull(exceptionHandler);
    }

    @Override
    public EventHandlerMeta<T> meta() {
        return meta;
    }

    @Override
    public void callEvent(T event) throws EventException {
        try {
            callable.callEvent(event);
        } catch(EventException ex) {
            throw ex;
        } catch(Throwable ex) {
            exceptionHandler.handleException(
                ex,
                "Exception dispatching " + event.getEventName() +
                " to " + callable.getClass().getSimpleName()
            );
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{meta=" + meta +
               " callable=" + callable +
               "}";
    }

    public static <T extends Event> CallableEventHandler<T> create(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, EventCallable<? super T> handler, ExceptionHandler exceptionHandler) {
        return new CallableEventHandler<>(new EventHandlerMeta<>(eventClass, priority, ignoreCancelled), handler, exceptionHandler);
    }

    public static <T extends Event> CallableEventHandler<T> create(Class<T> eventClass, EventPriority priority, EventCallable<? super T> handler, ExceptionHandler exceptionHandler) {
        return create(eventClass, priority, false, handler, exceptionHandler);
    }

    public static <T extends Event> CallableEventHandler<T> create(Class<T> eventClass, EventCallable<? super T> handler, ExceptionHandler exceptionHandler) {
        return create(eventClass, EventPriority.NORMAL, handler, exceptionHandler);
    }

    public static <T extends Event> CallableEventHandler<T> create(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, EventCallable<? super T> handler) {
        return create(eventClass, priority, ignoreCancelled, handler, LoggingExceptionHandler.forGlobalLogger());
    }

    public static <T extends Event> CallableEventHandler<T> create(Class<T> eventClass, EventPriority priority, EventCallable<? super T> handler) {
        return create(eventClass, priority, false, handler);
    }

    public static <T extends Event> CallableEventHandler<T> create(Class<T> eventClass, EventCallable<? super T> handler) {
        return create(eventClass, EventPriority.NORMAL, handler);
    }

    public static <T extends Event> CallableEventHandler<T> register(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, EventCallable<? super T> handler, ExceptionHandler exceptionHandler) {
        final CallableEventHandler<T> registeredHandler = create(eventClass, priority, ignoreCancelled, handler, exceptionHandler);
        Event.register(eventClass, registeredHandler);
        return registeredHandler;
    }

    public static <T extends Event> CallableEventHandler<T> register(Class<T> eventClass, EventPriority priority, EventCallable<? super T> handler, ExceptionHandler exceptionHandler) {
        return register(eventClass, priority, false, handler, exceptionHandler);
    }

    public static <T extends Event> CallableEventHandler<T> register(Class<T> eventClass, EventCallable<? super T> handler, ExceptionHandler exceptionHandler) {
        return register(eventClass, EventPriority.NORMAL, handler, exceptionHandler);
    }

    public static <T extends Event> CallableEventHandler<T> register(Class<T> eventClass, EventPriority priority, boolean ignoreCancelled, EventCallable<? super T> handler) {
        return register(eventClass, priority, ignoreCancelled, handler, LoggingExceptionHandler.forGlobalLogger());
    }

    public static <T extends Event> CallableEventHandler<T> register(Class<T> eventClass, EventPriority priority, EventCallable<? super T> handler) {
        return register(eventClass, priority, false, handler);
    }

    public static <T extends Event> CallableEventHandler<T> register(Class<T> eventClass, EventCallable<? super T> handler) {
        return register(eventClass, EventPriority.NORMAL, handler);
    }
}
