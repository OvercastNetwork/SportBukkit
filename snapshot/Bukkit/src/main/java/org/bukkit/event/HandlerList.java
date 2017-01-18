package org.bukkit.event;

import org.bukkit.plugin.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * A list of event handlers, stored per-event. Based on lahwran's fevents.
 */
public class HandlerList<T extends Event> {

    /**
     * Handler array. This field being an array is the key to this system's
     * speed.
     */
    private volatile RegisteredHandler<? super T>[] handlers = null;

    /**
     * Dynamic handler lists. These are changed using register() and
     * unregister() and are automatically baked to the handlers array any time
     * they have changed.
     */
    private final EnumMap<EventPriority, List<RegisteredHandler<? super T>>> handlerslots;

    /**
     * List of all HandlerLists which have been created, for use in bakeAll()
     */
    private static final ArrayList<HandlerList<?>> allLists = new ArrayList<>();

    /**
     * Bake all handler lists. Best used just after all normal event
     * registration is complete, ie just after all plugins are loaded if
     * you're using fevents in a plugin system.
     */
    public static void bakeAll() {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.bake();
            }
        }
    }

    /**
     * Unregister all listeners from all handler lists.
     */
    public static void unregisterAll() {
        synchronized (allLists) {
            for (HandlerList<?> h : allLists) {
                synchronized (h) {
                    h.handlerslots.values().forEach(List::clear);
                    h.handlers = null;
                }
            }
        }
    }

    /**
     * Unregister a specific plugin's listeners from all handler lists.
     *
     * @param plugin plugin to unregister
     */
    public static void unregisterAll(Plugin plugin) {
        unregisterAll(handler -> handler instanceof RegisteredListener &&
                                 plugin == ((RegisteredListener) handler).getPlugin());
    }

    /**
     * Unregister a specific listener from all handler lists.
     *
     * @param listener listener to unregister
     */
    public static void unregisterAll(Listener listener) {
        unregisterAll(handler -> handler instanceof BoundEventHandler &&
                                 listener == ((BoundEventHandler) handler).listener());
    }

    public static void unregisterAll(Predicate<RegisteredHandler<?>> filter) {
        synchronized(allLists) {
            for(HandlerList<?> list : allLists) {
                list.unregister(filter);
            }
        }
    }

    /**
     * Create a new handler list and initialize using EventPriority.
     * <p>
     * The HandlerList is then added to meta-list for use in bakeAll()
     */
    public HandlerList() {
        handlerslots = new EnumMap<>(EventPriority.class);
        for (EventPriority o : EventPriority.values()) {
            handlerslots.put(o, new ArrayList<>());
        }
        synchronized (allLists) {
            allLists.add(this);
        }
    }

    /**
     * Register a new listener in this handler list
     *
     * @param handler listener to register
     */
    public synchronized void register(RegisteredHandler<? super T> handler) {
        final EventPriority priority = handler.meta().priority();
        if(handlerslots.get(priority).contains(handler)) {
            throw new IllegalStateException("Event handler " + handler +
                                            " is already registered at priority " + priority);
        }
        handlers = null;
        handlerslots.get(priority).add(handler);
    }

    // For legacy binary compatibility
    public synchronized void register(RegisteredListener listener) {
        register((RegisteredHandler) listener);
    }

    /**
     * Register a collection of new listeners in this handler list
     *
     * @param handlers listeners to register
     */
    public void registerAll(Collection<RegisteredHandler<? super T>> handlers) {
        for(RegisteredHandler<? super T> listener : handlers) {
            register(listener);
        }
    }

    /**
     * Remove a listener from a specific order slot
     *
     * @param handler listener to remove
     */
    public synchronized void unregister(RegisteredHandler<? super T> handler) {
        if(handlerslots.get(handler.meta().priority()).remove(handler)) {
            handlers = null;
        }
    }

    /**
     * Remove a specific plugin's listeners from this handler
     *
     * @param plugin plugin to remove
     */
    public synchronized void unregister(Plugin plugin) {
        unregister(el -> el instanceof RegisteredListener && ((RegisteredListener) el).getPlugin().equals(plugin));
    }

    public synchronized void unregister(Predicate<? super RegisteredHandler<? super T>> filter) {
        boolean changed = false;
        for(List<RegisteredHandler<? super T>> list : handlerslots.values()) {
            for(ListIterator<RegisteredHandler<? super T>> i = list.listIterator(); i.hasNext();) {
                if (filter.test(i.next())) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if(changed) handlers = null;
    }

    /**
     * Remove a specific listener from this handler
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(Listener listener) {
        unregister(handler -> handler instanceof BoundEventHandler &&
                              listener == ((BoundEventHandler) handler).listener());
    }

    /**
     * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
     */
    public void bake() {
        getRegisteredListeners();
    }

    /**
     * Get the baked registered listeners associated with this handler list
     *
     * @return the array of registered listeners
     */
    public RegisteredHandler<? super T>[] getRegisteredListeners() {
        // Try without locking first
        RegisteredHandler<? super T>[] handlers = this.handlers;
        if(handlers != null) return handlers;

        synchronized(this) {
            handlers = this.handlers;
            if(handlers != null) return handlers;

            handlers = new RegisteredHandler[handlerslots.values().stream().mapToInt(List::size).sum()];
            int i = 0;
            for (Entry<EventPriority, List<RegisteredHandler<? super T>>> entry : handlerslots.entrySet()) {
                for(RegisteredHandler<? super T> handler : entry.getValue()) {
                    handlers[i++] = handler;
                }
            }
            this.handlers = handlers;
            return handlers;
        }
    }

    /**
     * Get a specific plugin's registered listeners associated with this
     * handler list
     *
     * @param plugin the plugin to get the listeners of
     * @return the list of registered listeners
     */
    public static ArrayList<RegisteredListener> getRegisteredListeners(Plugin plugin) {
        final ArrayList<RegisteredListener> results = new ArrayList<RegisteredListener>();
        synchronized(allLists) {
            for(HandlerList<?> list : allLists) {
                list.collectRegisteredListeners(plugin, results);
            }
        }
        return results;
    }

    private synchronized void collectRegisteredListeners(Plugin plugin, List<RegisteredListener> results) {
        for(List<RegisteredHandler<? super T>> handlers : handlerslots.values()) {
            for(RegisteredHandler<? super T> handler : handlers) {
                if(handler instanceof RegisteredListener) {
                    final RegisteredListener rl = (RegisteredListener) handler;
                    if(plugin.equals(rl.getPlugin())) {
                        results.add(rl);
                    }
                }
            }
        }
    }

    /**
     * Get a list of all handler lists for every event type
     *
     * @return the list of all handler lists
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<HandlerList> getHandlerLists() {
        synchronized (allLists) {
            return (ArrayList<HandlerList>) allLists.clone();
        }
    }
}
