package org.bukkit.event;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import tc.oc.exception.ExceptionHandler;
import org.bukkit.exception.TestExceptionHandler;
import tc.oc.test.TestCodeBlock;
import org.junit.Test;

import static tc.oc.test.Assert.*;
import static org.junit.Assert.*;

public class EventMethodExecutorTest {

    ExceptionHandler exceptionHandler = new TestExceptionHandler();

    List<EventMethodExecutor<?>> createHandlers(Class<? extends Listener> listener) {
        return EventMethodExecutor.forMethods(listener, exceptionHandler)
                                  .collect(Collectors.toList());
    }

    @Test
    public void empty() throws Exception {
        class C implements Listener {}
        assertEmpty(createHandlers(C.class));
    }

    @Test
    public void singleHandler() throws Exception {
        class C implements Listener {
            @EventHandler
            public void handler(TestEvent event) {}
        }
        assertSize(1, createHandlers(C.class));
    }

    @Test
    public void nonPublicHandlers() throws Exception {
        class C implements Listener {
            @EventHandler private void privateHandler(TestEvent event) {}
            @EventHandler protected void protectedHandler(TestEvent event) {}
            @EventHandler void packageHandler(TestEvent event) {}
        }
        assertSize(3, createHandlers(C.class));
    }

    @Test
    public void superclassHandler() throws Exception {
        class C implements Listener {
            @EventHandler
            public void handler(TestEvent event) {}
        }
        class D extends C {}
        assertSize(1, createHandlers(D.class));
    }

    interface I extends Listener {
        @EventHandler
        default void handler(TestEvent event) {}
    }

    @Test
    public void interfaceHandler() throws Exception {
        assertSize(1, createHandlers(I.class));
    }

    @Test
    public void inheritedInterfaceHandler() throws Exception {
        class C implements I {}
        assertSize(1, createHandlers(C.class));
    }

    static class PrivateHandler implements Listener {
        boolean called = false;

        @EventHandler
        private void handler(TestEvent event) {
            called = true;
        }
    }

    @Test
    public void callPrivateHandler() throws Throwable {
        PrivateHandler listener = new PrivateHandler();
        EventMethodExecutor.forMethod(
            PrivateHandler.class.getDeclaredMethod("handler", TestEvent.class),
            exceptionHandler
        ).execute(listener, new TestEvent());

        assertTrue(listener.called);
    }

    @Test
    public void reportException() throws Throwable {
        class C implements Listener {
            @EventHandler
            void handler(TestEvent event) throws Exception {
                throw new Exception();
            }
        }

        AtomicBoolean reported = new AtomicBoolean(false);

        EventMethodExecutor.forMethod(
            C.class.getDeclaredMethod("handler", TestEvent.class),
            (exception, message) -> reported.set(true)
        ).execute(new C(), new TestEvent());

        assertTrue(reported.get());
    }

    @Test
    public void propagateEventException() throws Throwable {
        class C implements Listener {
            @EventHandler
            void handler(TestEvent event) throws Exception {
                throw new EventException();
            }
        }

        EventMethodExecutor executor = EventMethodExecutor.forMethod(
            C.class.getDeclaredMethod("handler", TestEvent.class),
            exceptionHandler
        );

        assertThrows(EventException.class, new TestCodeBlock() { // lambda here crashes the compiler
            @Override
            public void run() throws Throwable {
                executor.execute(new C(), new TestEvent());
            }
        });
    }
}
