package org.bukkit.craftbukkit.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.ChatBaseComponent;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.PacketDataSerializer;
import org.bukkit.craftbukkit.util.BungeeChatUtils;
import org.bukkit.entity.Player;

import static com.google.common.base.Preconditions.checkNotNull;

public class RenderableComponent extends ChatBaseComponent {

    private final BaseComponent original;
    private final Map<UUID, BaseComponent> rendered = new HashMap<>();

    public static @Nullable RenderableComponent wrap(@Nullable IChatBaseComponent nms) {
        if(nms == null) return null;
        if(nms instanceof RenderableComponent) {
            return (RenderableComponent) nms;
        }
        return new RenderableComponent(BungeeChatUtils.toBungee(nms));
    }

    public static void render(@Nullable IChatBaseComponent nms, Player viewer) {
        if(nms instanceof RenderableComponent) {
            ((RenderableComponent) nms).render(viewer);
        }
    }

    public RenderableComponent(BaseComponent bukkit) {
        this.original = checkNotNull(bukkit);
    }

    protected BaseComponent afterRender(BaseComponent text) {
        return text;
    }

    public void render(Player viewer) {
        rendered.computeIfAbsent(
            viewer.getUniqueId(),
            uuid -> afterRender(
                viewer.getServer()
                      .textRenderContext()
                      .render(original, viewer)
            )
        );
    }

    public void serialize(PacketDataSerializer data) {
        data.a(ComponentSerializer.toString(rendered.getOrDefault(data.playerId, original)));
    }

    @Override
    public String getText() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IChatBaseComponent f() {
        throw new UnsupportedOperationException();
    }
}
