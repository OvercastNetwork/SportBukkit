package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerUseUnknownEntityEvent extends PlayerActionBase {

    private final int entityId;
    private final boolean attack;
    private final EquipmentSlot hand;

    public PlayerUseUnknownEntityEvent(Player who, int entityId, boolean attack, EquipmentSlot hand) {
        super(who);
        this.entityId = entityId;
        this.attack = attack;
        this.hand = hand;
    }

    public int getEntityId() {
        return entityId;
    }

    public boolean isAttack() {
        return attack;
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() { return handlers; }
    @Override public HandlerList getHandlers() { return handlers; }
}
