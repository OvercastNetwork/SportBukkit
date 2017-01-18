package org.bukkit.event;

import java.lang.reflect.Method;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.inject.Inject;

import tc.oc.exception.ExceptionHandler;
import tc.oc.exception.LoggingExceptionHandler;
import tc.oc.minecraft.api.event.Listener;

public class SimpleEventRegistry implements EventRegistry {

    private final ExceptionHandler exceptionHandler;

    public SimpleEventRegistry() {
        this(null);
    }

    @Inject public SimpleEventRegistry(@Nullable ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler
                                                         : LoggingExceptionHandler.forGlobalLogger();
    }

    @Override
    public <T extends Event> BoundEventHandler<T> bindHandler(EventHandlerMeta<T> meta, Listener listener, EventExecutor<T> executor) {
        return new OwnedEventHandler<>(meta, executor, listener);
    }

    @Override
    public <T extends Event> BoundEventHandler<T> bindHandler(Listener listener, EventMethodExecutor<T> executor) {
        return new OwnedEventHandler<>(executor, listener);
    }

    @Override
    public <T extends Event> EventMethodExecutor<T> createHandler(EventHandlerMeta<T> meta, Method method) {
        return new EventMethodExecutor<>(meta, method, exceptionHandler);
    }

    @Override
    public EventMethodExecutor<?> createHandler(Method method) {
        return EventMethodExecutor.forMethod(method, exceptionHandler);
    }

    @Override
    public Stream<EventMethodExecutor<?>> createHandlers(Class<? extends Listener> listener) {
        return EventMethodExecutor.forMethods(listener, exceptionHandler);
    }

    @Override
    public Stream<BoundEventHandler<?>> bindHandlers(Listener listener) {
        return createHandlers(listener.getClass())
            .map(unbound -> bindHandler(listener, unbound));
    }

    @Override
    public void registerListener(Listener listener) {
        bindHandlers(listener).forEach(handler -> Event.register(handler));
    }

    @Override
    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(handler -> handler instanceof OwnedEventHandler &&
                                             this == ((OwnedEventHandler) handler).registry() &&
                                             listener == ((OwnedEventHandler) handler).listener());
    }

    @Override
    public void unregisterAll() {
        HandlerList.unregisterAll(handler -> handler instanceof OwnedEventHandler &&
                                             this == ((OwnedEventHandler) handler).registry());
    }

    private class OwnedEventHandler<T extends Event> extends BoundEventHandler<T> {

        public OwnedEventHandler(EventMethodExecutor<T> executor, Listener listener) {
            super(executor, listener);
        }

        public OwnedEventHandler(EventHandlerMeta<T> meta, EventExecutor<T> executor, Listener listener) {
            super(meta, executor, listener);
        }

        SimpleEventRegistry registry() {
            return SimpleEventRegistry.this;
        }
    }
}
