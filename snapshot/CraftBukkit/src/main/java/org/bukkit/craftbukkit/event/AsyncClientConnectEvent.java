package org.bukkit.craftbukkit.event;

import io.netty.channel.Channel;
import net.minecraft.server.NetworkManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called right after a new client connection is initialized,
 * before any packets have been exchanged.
 *
 * Obviously, this is quite hacky. Please use for good and not evil.
 */
public class AsyncClientConnectEvent extends Event {

    private final Channel channel;
    private final NetworkManager networkManager;

    public AsyncClientConnectEvent(Channel channel, NetworkManager networkManager) {
        super(true);
        this.channel = channel;
        this.networkManager = networkManager;
    }

    public Channel channel() {
        return channel;
    }

    public NetworkManager networkManager() {
        return networkManager;
    }

    private static final HandlerList<AsyncClientConnectEvent> handlers = new HandlerList<>();
}
