package org.bukkit.event;

import javax.annotation.Nullable;

public class SimpleEventBus implements EventBus {

    private final Thread primaryThread;
    private final Object lock;

    public SimpleEventBus() {
        this(null, null);
    }

    public SimpleEventBus(@Nullable Thread primaryThread, @Nullable Object lock) {
        this.primaryThread = primaryThread != null ? primaryThread : Thread.currentThread();
        this.lock = lock != null ? lock : new Object();
    }

    @Override
    public void callEvent(Event event) {
        callEvent(event, null, null);
    }

    @Override
    public void callEvent(Event event, @Nullable EventPriority priority) {
        callEvent(event, priority, null);
    }

    @Override
    public <T extends Event, X extends Throwable> void callEvent(T event, @Nullable EventBody<? super T, X> body) throws X {
        callEvent(event, null, body);
    }

    @Override
    public <T extends Event, X extends Throwable> void callEvent(T event, @Nullable EventPriority priority, @Nullable EventBody<? super T, X> body) throws X {
        if(event.isAsynchronous()) {
            if(Thread.holdsLock(lock)) {
                throw new IllegalStateException(event.getEventName() + " cannot be called asynchronously from inside synchronized code.");
            }
            if(Thread.currentThread().equals(primaryThread)) {
                throw new IllegalStateException(event.getEventName() + " cannot be called asynchronously from primary thread.");
            }
            callEvent0(event, priority, body);
        } else {
            synchronized(lock) {
                callEvent0(event, priority, body);
            }
        }
    }

    public <T extends Event, X extends Throwable> void callEvent0(T event, @Nullable EventPriority priority, @Nullable EventBody<? super T, X> body) throws X {
        try {
            callEvent0(event, priority, event.getHandlers().getRegisteredListeners(), 0, body);
        } catch(EventException e) {
            throw (X) e.getCause();
        }
    }

    private <T extends Event, X extends Throwable> void callEvent0(T event, @Nullable EventPriority priority, RegisteredHandler<? super T>[] handlers, int index, @Nullable EventBody<? super T, X> body) throws EventException {
        for(int i = index; i < handlers.length; i++) {
            final RegisteredHandler<? super T> handler = handlers[i];
            if(handler.canHandle(event, priority)) {
                // When calling a handler, pass a continuation that recurses into this method
                // with the current index, which will continue the loop. If the handler yields
                // (which makes callEventHandler0 return true), then return immediately, since
                // the nested call will have already finished the entire dispatch process.

                // By only recursing when the handler yields, we avoid unnecessary stack growth,
                // which can be appreciated when reading stack traces.

                final int nextIndex = i + 1;
                if(callEventHandler0(event, handler, ev -> callEvent0(event, priority, handlers, nextIndex, body))) return;
            }
        }

        // If we get here, it means we got to the end of the handler array without any handler yielding.
        // This will happen exactly once per event, in the inner-most call to this method. Any outer calls
        // will return early in the loop above.
        if(body != null) {
            try {
                body.callEvent(event);
            } catch(Throwable ex) {
                // Exceptions from the event body are wrapped and thrown all the way back
                // to the start of the dispatch, where they are unwrapped and rethrown.
                // For this to work, any yielding handlers need to let the EventException
                // propagate from the yield() method.
                throw new EventException(ex, event);
            }
        }
    }

    @Override
    public <T extends Event, X extends Throwable> boolean callEventHandler(T event, @Nullable EventPriority priority, RegisteredHandler<? super T> handler, @Nullable EventBody<? super T, X> body) throws X {
        if(handler.canHandle(event, priority)) {
            try {
                return callEventHandler0(event, handler, body);
            } catch(EventException e) {
                throw (X) e.getCause();
            }
        }
        return false;
    }

    private <T extends Event> boolean callEventHandler0(T event, RegisteredHandler<? super T> handler, @Nullable EventCallable yielder) throws EventException {
        final EventCallable oldYielder = event.yielder;
        if(yielder == null) yielder = EventCallable.EMPTY;
        event.yielder = yielder;
        try {
            handler.callEvent(event);
            return event.yielder != yielder;
        } finally {
            event.yielder = oldYielder;
        }
    }
}
