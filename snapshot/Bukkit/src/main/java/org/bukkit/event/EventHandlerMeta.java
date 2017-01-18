package org.bukkit.event;

import java.lang.reflect.Method;
import java.util.Objects;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parameters for event handler registration
 */
public class EventHandlerMeta<T extends Event> {

    private final Class<T> event;
    private final EventPriority priority;
    private final boolean ignoreCancelled;

    public EventHandlerMeta(Class<T> event, EventPriority priority, boolean ignoreCancelled) {
        this.event = checkNotNull(event);
        this.priority = checkNotNull(priority);
        this.ignoreCancelled = ignoreCancelled;
    }

    /**
     * Base {@link Event} type that this handler can handle
     */
    public Class<T> event() {
        return event;
    }

    /**
     * Priority level at which this handler should be called
     */
    public EventPriority priority() {
        return priority;
    }

    /**
    * Whether this handler accepts cancelled events
    */
    public boolean ignoreCancelled() {
        return ignoreCancelled;
    }

    public boolean canHandle(Event event, @Nullable EventPriority priority) {
        return this.event.isInstance(event) &&
               (priority == null || priority.equals(this.priority)) &&
               !(event.isCancelled() && this.ignoreCancelled);
    }

    public boolean canCall(Method method) {
        return method.getParameterTypes().length == 1 &&
               method.getParameterTypes()[0].isAssignableFrom(event());
    }

    public void assertCanCall(Method method) {
        if(!canCall(method)) {
            throw new IllegalArgumentException(
                "Invalid event handler method signature " + method.toGenericString() +
                " in " + method.getDeclaringClass().getName()
            );
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, priority, ignoreCancelled);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof EventHandlerMeta)) return false;
        final EventHandlerMeta that = (EventHandlerMeta) obj;
        return this.event.equals(that.event()) &&
               this.priority.equals(that.priority()) &&
               this.ignoreCancelled == that.ignoreCancelled();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{event=" + event.getSimpleName() +
               " priority=" + priority.name() +
               " ignoreCancelled=" + ignoreCancelled +
               "}";
    }

    /**
     * Create an {@link EventHandlerMeta} for the given event class, copying other
     * parameters from the given {@link EventHandler} annotation.
     */
    public static <T extends Event> EventHandlerMeta<T> forAnnotation(Class<T> event, EventHandler annotation) {
        return new EventHandlerMeta<>(event, annotation.priority(), annotation.ignoreCancelled());
    }

    /**
     * Reflect on the given {@link Method} and create a {@link EventHandlerMeta} for it,
     * using the method's signature and annotations.
     *
     * If the method is not annotated as an {@link EventHandler}, return null.
     */
    public static @Nullable EventHandlerMeta<?> forMethod(Method method) {
        if(method.isBridge() || method.isSynthetic()) return null;

        final EventHandler annotation = method.getAnnotation(EventHandler.class);
        if(annotation == null) return null;

        final Class<?> eventClass;
        if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(eventClass = method.getParameterTypes()[0])) {
            throw new IllegalArgumentException("Invalid @EventHandler method " + method.toGenericString() +
                                               " in " + method.getDeclaringClass().getName());
        }

        // Make sure the event has a HandlerList
        Event.getHandlerList((Class<? extends Event>) eventClass);

        return forAnnotation(eventClass.asSubclass(Event.class), annotation);
    }
}
