package org.bukkit.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import tc.oc.exception.ExceptionHandler;
import org.bukkit.exception.TestExceptionHandler;
import tc.oc.test.TestThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static tc.oc.test.Assert.*;
import static org.junit.Assert.*;

public class EventBusTest {

    static class Oops extends Exception {}

    ExceptionHandler exceptionHandler = new TestExceptionHandler();
    SimpleEventBus bus;
    List<String> flow;

    @Before
    public void setUp() throws Exception {
        bus = new SimpleEventBus(Thread.currentThread(), null);
        flow = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception {
        HandlerList.unregisterAll();
    }

    void flow(String action) {
        flow.add(action);
    }

    void assertFlow(String... actions) {
        assertEquals(Arrays.asList(actions), flow);
    }

    void register(EventCallable<TestEvent> handler) {
        CallableEventHandler.register(TestEvent.class, handler, exceptionHandler);
    }

    void register(EventPriority priority, EventCallable<TestEvent> handler) {
        CallableEventHandler.register(TestEvent.class, priority, handler, exceptionHandler);
    }

    @Test
    public void handleSyncEvent() {
        register(event ->
            flow("handler")
        );
        bus.callEvent(new TestEvent());
        assertFlow("handler");
    }

    @Test
    public void handleAsyncEvent() throws Throwable {
        register(event ->
            flow("handler")
        );
        TestThread.join(() ->
            bus.callEvent(new TestEvent(true))
        );
        assertFlow("handler");
    }

    @Test
    public void asyncEventOnMainThreadThrows() throws Throwable {
        assertThrows(IllegalStateException.class, () ->
            bus.callEvent(new TestEvent(true))
        );
    }

    @Test
    public void eventWithBody() throws Exception {
        bus.callEvent(new TestEvent(), event ->
            flow("body")
        );
        assertFlow("body");
    }

    @Test
    public void nonYieldingHandler() throws Exception {
        register(event ->
            flow("handler")
        );
        bus.callEvent(new TestEvent(), event ->
            flow("body")
        );
        assertFlow("handler", "body");
    }

    @Test
    public void yieldingHandler() throws Exception {
        register(event -> {
            flow("before");
            event.yield();
            flow("after");
        });

        bus.callEvent(new TestEvent(), event ->
            flow("body")
        );

        assertFlow("before", "body", "after");
    }

    @Test
    public void yieldMultipleTimesThrows() throws Throwable {
        register(event -> {
            event.yield();
            assertThrows(IllegalStateException.class, event::yield);
        });

        bus.callEvent(new TestEvent());
    }

    @Test
    public void mixedYieldingAndNonYieldingHandlers() throws Exception {
        register(EventPriority.LOW, event -> {
            flow("low");
        });

        register(EventPriority.NORMAL, event -> {
            flow("normal before");
            event.yield();
            flow("normal after");
        });

        register(EventPriority.HIGH, event -> {
            flow("high");
        });

        bus.callEvent(new TestEvent(), event ->
            flow("body")
        );

        assertFlow(
            "low",
            "normal before",
            "high",
            "body",
            "normal after"
        );
    }

    @Test
    public void yieldAtMultiplePriorityLevels() throws Exception {
        Stream.of(EventPriority.LOW, EventPriority.NORMAL, EventPriority.HIGH).forEach(priority -> {
            register(priority, event -> {
                flow("before " + priority);
                event.yield();
                flow("after " + priority);
            });
        });

        bus.callEvent(new TestEvent(), event ->
            flow("body")
        );

        assertFlow(
            "before LOW",
            "before NORMAL",
            "before HIGH",
            "body",
            "after HIGH",
            "after NORMAL",
            "after LOW"
        );
    }

    @Test
    public void eventBodyException() throws Throwable {
        register(Event::yield); // A handler that just yields

        assertThrows(Oops.class, () ->
            bus.callEvent(new TestEvent(), event -> {
                throw new Oops();
            })
        );
    }

    @Test
    public void handlerExceptionBeforeYield() throws Exception {
        ExceptionHandler exceptionHandler = (exception, message) -> flow("report");

        CallableEventHandler.register(TestEvent.class, EventPriority.LOW, event -> {
            flow("low handler before");
            event.yield();
            flow("low handler after");
        }, exceptionHandler);

        CallableEventHandler.register(TestEvent.class, EventPriority.NORMAL, event -> {
            flow("bad handler");
            throw new RuntimeException();
        }, exceptionHandler);

        CallableEventHandler.register(TestEvent.class, EventPriority.HIGH, event -> {
            flow("high handler before");
            event.yield();
            flow("high handler after");
        }, exceptionHandler);

        bus.callEvent(new TestEvent(), event ->
            flow("body")
        );

        assertFlow(
            "low handler before",
            "bad handler",
            "report",
            "high handler before",
            "body",
            "high handler after",
            "low handler after"
        );
    }

    @Test
    public void handlerExceptionAfterYield() throws Exception {
        ExceptionHandler exceptionHandler = (exception, message) -> flow("report");

        CallableEventHandler.register(TestEvent.class, EventPriority.LOW, event -> {
            flow("low handler before");
            event.yield();
            flow("low handler after");
        }, exceptionHandler);

        CallableEventHandler.register(TestEvent.class, EventPriority.NORMAL, event -> {
            flow("bad handler before");
            event.yield();
            flow("bad handler after");
            throw new RuntimeException();
        }, exceptionHandler);

        CallableEventHandler.register(TestEvent.class, EventPriority.HIGH, event -> {
            flow("high handler before");
            event.yield();
            flow("high handler after");
        }, exceptionHandler);

        bus.callEvent(new TestEvent(), event ->
            flow("body")
        );

        assertFlow(
            "low handler before",
            "bad handler before",
            "high handler before",
            "body",
            "high handler after",
            "bad handler after",
            "report",
            "low handler after"
        );
    }
}
