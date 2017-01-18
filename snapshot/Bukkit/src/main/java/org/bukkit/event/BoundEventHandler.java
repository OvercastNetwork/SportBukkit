package org.bukkit.event;

import java.util.Objects;

import tc.oc.minecraft.api.event.Listener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link RegisteredHandler} associated with a {@link Listener}, that passes events
 * to an {@link EventMethodExecutor}, along with that listener.
 */
public class BoundEventHandler<T extends Event> implements RegisteredHandler<T> {

    private final EventHandlerMeta<T> meta;
    private final EventExecutor<T> executor;
    private final Listener listener;

    public BoundEventHandler(EventMethodExecutor<T> executor, Listener listener) {
        this(executor.meta(), executor, listener);

        if(!executor.method().getDeclaringClass().isInstance(listener)) {
            throw new IllegalArgumentException(
                "Cannot bind event handler method " + executor +
                " to listener of type " + listener.getClass().getName() +
                " because it is not assignable to " + executor.method().getDeclaringClass().getName()
            );
        }
    }

    public BoundEventHandler(EventHandlerMeta<T> meta, EventExecutor<T> executor, Listener listener) {
        this.meta = checkNotNull(meta);
        this.executor = checkNotNull(executor);
        this.listener = checkNotNull(listener);
    }

    @Override
    public EventHandlerMeta<T> meta() {
        return meta;
    }

    public Listener listener() {
        return listener;
    }

    @Override
    public void callEvent(T event) throws EventException {
        executor.execute(listener, event);
    }

    @Override
    final public int hashCode() {
        return Objects.hash(meta, executor, listener);
    }

    @Override
    final public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof BoundEventHandler)) return false;
        final BoundEventHandler that = (BoundEventHandler) obj;
        return this.meta.equals(that.meta) &&
               this.executor.equals(that.executor) &&
               this.listener == that.listener;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{meta=" + meta +
               " executor=" + executor +
               " listener=" + listener +
               "}";
    }
}
