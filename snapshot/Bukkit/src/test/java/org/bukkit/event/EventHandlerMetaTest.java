package org.bukkit.event;

import org.junit.Test;

import static tc.oc.test.Assert.*;
import static org.junit.Assert.*;

public class EventHandlerMetaTest {

    @Test
    public void canHandle() throws Throwable {
        EventHandlerMeta<TestEvent> meta = new EventHandlerMeta<>(TestEvent.class, EventPriority.NORMAL, false);

        // Base case
        assertTrue(meta.canHandle(new TestEvent(), null));

        // Event type
        assertFalse(meta.canHandle(new Event(){}, null));
        assertTrue(meta.canHandle(new TestEvent(){}, null));

        // Priority
        assertTrue(meta.canHandle(new TestEvent(), EventPriority.NORMAL));
        assertFalse(meta.canHandle(new TestEvent(), EventPriority.LOW));

        // Cancelled
        TestEvent cancelled = new TestEvent(){
            public boolean isCancelled() { return true; }
        };
        assertTrue(meta.canHandle(cancelled, null));
        assertFalse(new EventHandlerMeta<>(TestEvent.class, EventPriority.NORMAL, true).canHandle(cancelled, null));
    }

    @Test
    public void createForMethod() throws Exception {
        class C {
            @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
            void handler(TestEvent event) {}
        }

        EventHandlerMeta<?> meta = EventHandlerMeta.forMethod(C.class.getDeclaredMethod("handler", TestEvent.class));

        assertEquals(TestEvent.class, meta.event());
        assertEquals(EventPriority.HIGH, meta.priority());
        assertEquals(true, meta.ignoreCancelled());
    }

    @Test
    public void ignoreMethodWithoutAnnotation() throws Exception {
        class C {
            void handler(TestEvent event) {}
        }
        assertNull(EventHandlerMeta.forMethod(C.class.getDeclaredMethod("handler", TestEvent.class)));
    }

    @Test
    public void missingEventParameter() throws Throwable {
        class C {
            @EventHandler
            void handler() {}
        }
        assertThrows(IllegalArgumentException.class, () ->
            EventHandlerMeta.forMethod(C.class.getDeclaredMethod("handler"))
        );
    }

    @Test
    public void extraParameter() throws Throwable {
        class C {
            @EventHandler
            void handler(TestEvent event, int what) {}
        }
        assertThrows(IllegalArgumentException.class, () ->
            EventHandlerMeta.forMethod(C.class.getDeclaredMethod("handler", TestEvent.class, int.class))
        );
    }

    @Test
    public void abstractEvent() throws Throwable {
        class C {
            @EventHandler
            void handler(Event event) {}
        }
        assertThrows(IllegalArgumentException.class, () ->
            EventHandlerMeta.forMethod(C.class.getDeclaredMethod("handler", Event.class))
        );
    }
}
