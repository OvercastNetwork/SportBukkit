package org.bukkit.event;

import tc.oc.minecraft.api.event.Listener;

public interface EventExecutor<T extends Event> {
    void execute(Listener listener, T event) throws EventException;
}
