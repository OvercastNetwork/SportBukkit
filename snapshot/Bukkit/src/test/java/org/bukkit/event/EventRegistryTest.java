package org.bukkit.event;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static tc.oc.test.Assert.*;
import static org.junit.Assert.*;

public class EventRegistryTest {

    EventRegistry registry;

    @Before
    public void setUp() throws Exception {
        registry = new SimpleEventRegistry();
    }

    @After
    public void tearDown() throws Exception {
        HandlerList.unregisterAll();
    }

    List<EventMethodExecutor<?>> createHandlers(Class<? extends Listener> listener) {
        return registry.createHandlers(listener).collect(Collectors.toList());
    }

    void assertRegistered(Predicate<? super RegisteredHandler<?>> filter) {
        assertRegistered(1, filter);
    }

    void assertNotRegistered(Predicate<? super RegisteredHandler<?>> filter) {
        assertRegistered(0, filter);
    }

    void assertRegistered(int count, Predicate<? super RegisteredHandler<?>> filter) {
        assertEquals(count, Stream.of(Event.getHandlerList(TestEvent.class)
                                           .getRegisteredListeners())
                                  .filter(filter)
                                  .count());
    }

    @Test
    public void registerListener() throws Exception {
        class C implements Listener {
            @EventHandler
            void handler(TestEvent event) {}
        }
        C c = new C();
        registry.registerListener(c);
        assertRegistered(handler -> handler instanceof BoundEventHandler &&
                                    ((BoundEventHandler) handler).listener() == c);
    }

    @Test
    public void unregisterListener() throws Exception {
        class C implements Listener {
            @EventHandler
            void handler(TestEvent event) {}
        }
        C c1 = new C();
        C c2 = new C();
        registry.registerListener(c1);
        registry.registerListener(c2);
        registry.unregisterListener(c1);

        assertNotRegistered(handler -> handler instanceof BoundEventHandler && ((BoundEventHandler) handler).listener() == c1);
        assertRegistered(handler -> handler instanceof BoundEventHandler && ((BoundEventHandler) handler).listener() == c2);
    }

    @Test
    public void unregisterAll() throws Exception {
        class C implements Listener {
            @EventHandler
            void handler(TestEvent event) {}
        }
        EventRegistry registry1 = new SimpleEventRegistry();
        EventRegistry registry2 = new SimpleEventRegistry();
        C c1 = new C();
        C c2 = new C();
        registry1.registerListener(c1);
        registry2.registerListener(c2);
        registry1.unregisterAll();

        assertNotRegistered(handler -> handler instanceof BoundEventHandler && ((BoundEventHandler) handler).listener() == c1);
        assertRegistered(handler -> handler instanceof BoundEventHandler && ((BoundEventHandler) handler).listener() == c2);
    }
}
