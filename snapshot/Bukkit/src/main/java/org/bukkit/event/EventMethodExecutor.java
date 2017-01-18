package org.bukkit.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import tc.oc.exception.ExceptionHandler;
import tc.oc.minecraft.api.event.Listener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@link EventExecutor} that reflectively calls an instance {@link Method}
 * on the {@link Listener} passed to it with the event.
 *
 * Events that do not match the provided {@link EventHandlerMeta} are silently ignored.
 *
 * Exceptions thrown by the method are passed to the provided {@link ExceptionHandler},
 * except for {@link EventException}, which is propagated.
 */
public class EventMethodExecutor<T extends Event> implements EventExecutor<T> {

    private final EventHandlerMeta<T> meta;
    private final Method method;
    private final ExceptionHandler exceptionHandler;

    public EventMethodExecutor(EventHandlerMeta<T> meta, Method method, ExceptionHandler exceptionHandler) {
        this.meta = checkNotNull(meta);
        this.method = checkNotNull(method);
        this.exceptionHandler = checkNotNull(exceptionHandler);

        this.meta.assertCanCall(this.method);
        this.method.setAccessible(true);
    }

    public EventHandlerMeta<T> meta() {
        return meta;
    }

    public Method method() {
        return method;
    }

    public BoundEventHandler<T> bind(Listener listener) {
        return new BoundEventHandler<>(meta, this, listener);
    }

    public void execute(Listener listener, T event) throws EventException {
        if(meta.canHandle(event, null)) {
            try {
                method.invoke(listener, event);
            } catch(InvocationTargetException ex) {
                if(ex.getCause() instanceof EventException) {
                    throw (EventException) ex.getCause();
                }
                handleException(listener, event, ex.getCause());
            } catch(Throwable ex) {
                handleException(listener, event, ex);
            }
        }
    }

    protected void handleException(Listener listener, T event, Throwable ex) {
        exceptionHandler.handleException(ex, "Exception dispatching event " + event.getEventName() + " to " + listener);
    }

    @Override
    final public int hashCode() {
        return Objects.hash(meta, method);
    }

    @Override
    final public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof EventMethodExecutor)) return false;
        final EventMethodExecutor that = (EventMethodExecutor) obj;
        return this.meta.equals(that.meta) &&
               this.method.equals(that.method);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{meta=" + meta +
               " method=" + method +
               "}";
    }

    /**
     * Reflect on the given {@link Method} and create a {@link EventMethodExecutor} for it,
     * using the method's signature and annotations.
     *
     * If the method is not annotated as an {@link EventHandler}, return null.
     */
    public static @Nullable EventMethodExecutor forMethod(Method method, ExceptionHandler exceptionHandler) {
        final EventHandlerMeta<?> meta = EventHandlerMeta.forMethod(method);
        if(meta == null) return null;
        return new EventMethodExecutor<>(meta, method, exceptionHandler);
    }

    /**
     * Create {@link EventMethodExecutor}s for all event handler methods found in the given {@link Listener} class.
     *
     * This includes any methods inherited from superclasses or interfaces.
     */
    public static Stream<EventMethodExecutor<?>> forMethods(Class<? extends Listener> listener, ExceptionHandler exceptionHandler) {
        return Stream.concat(
            Stream.of(listener.getDeclaredMethods())
                  .map(method -> forMethod(method, exceptionHandler))
                  .filter(Objects::nonNull),
            Stream.concat(Stream.of(listener.getSuperclass()),
                          Stream.of(listener.getInterfaces()))
                  .filter(ancestor -> ancestor != null && Listener.class.isAssignableFrom(ancestor))
                  .flatMap(ancestor -> forMethods((Class<? extends Listener>) ancestor, exceptionHandler))
        );
    }
}
