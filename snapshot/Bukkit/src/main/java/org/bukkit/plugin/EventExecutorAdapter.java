package org.bukkit.plugin;

import org.bukkit.event.*;
import tc.oc.minecraft.api.event.Listener;

class EventExecutorAdapter<T extends Event> implements org.bukkit.event.EventExecutor<T> {

    private final EventExecutor legacy;

    EventExecutorAdapter(EventExecutor legacy) {
        this.legacy = legacy;
    }

    @Override
    public void execute(Listener listener, T event) throws EventException {
        legacy.execute((org.bukkit.event.Listener) listener, event);
    }
}
