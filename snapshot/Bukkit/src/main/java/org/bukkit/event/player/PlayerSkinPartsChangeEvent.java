package org.bukkit.event.player;

import org.bukkit.Skin;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class PlayerSkinPartsChangeEvent extends PlayerEvent {

    private final Set<Skin.Part> parts;

    public PlayerSkinPartsChangeEvent(Player who, Set<Skin.Part> parts) {
        super(who);
        this.parts = parts;
    }

    public Set<Skin.Part> getParts() {
        return parts;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
