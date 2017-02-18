package org.bukkit.craftbukkit.protocol;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.server.ServerPing;

public class CraftServerPing extends ServerPing {

    private final Map<String, JsonElement> extra = new HashMap<>();

    public void put(String name, JsonElement value) {
        extra.put(name, value);
    }

    private static final ServerPing.Serializer parentSerializer = new ServerPing.Serializer();

    public static class Serializer implements JsonSerializer<CraftServerPing> {
        @Override
        public JsonElement serialize(CraftServerPing ping, Type type, JsonSerializationContext context) {
            final JsonObject json = (JsonObject) parentSerializer.serialize(ping, ServerPing.class, context);
            ping.extra.forEach(json::add);
            return json;
        }
    }
}
