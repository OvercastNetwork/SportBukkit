package org.bukkit.event;

import org.junit.Test;

import static tc.oc.test.Assert.*;

public class HandlerListTest {

    @Test
    public void duplicateRegistration() throws Throwable {
        HandlerList list = Event.getHandlerList(TestEvent.class);
        CallableEventHandler<TestEvent> handler = CallableEventHandler.create(TestEvent.class, event -> {});
        list.register(handler);
        assertThrows(IllegalStateException.class, () ->
            list.register(handler)
        );
    }
}
