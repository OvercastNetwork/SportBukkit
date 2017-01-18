package org.bukkit.craftbukkit.protocol;

import java.io.IOException;

import net.minecraft.server.Packet;
import net.minecraft.server.PacketDataSerializer;
import net.minecraft.server.PacketListenerPlayOut;

/**
 * This packet is used only as a placeholder. It is never instantiated,
 * except once in EnumProtocol as a check.
 */
public class PacketPlayOutUpdateSign implements Packet<PacketListenerPlayOut> {
    @Override public void a(PacketDataSerializer packetdataserializer) throws IOException {}
    @Override public void b(PacketDataSerializer packetdataserializer) throws IOException {}
    @Override public void a(PacketListenerPlayOut t0) {}
}
