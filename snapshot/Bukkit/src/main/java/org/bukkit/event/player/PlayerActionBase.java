package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.PlayerAction;

public abstract class PlayerActionBase extends PlayerEvent implements PlayerAction {

    public PlayerActionBase(Player who) {
        super(who);
    }

    PlayerActionBase(Player who, boolean async) {
        super(who, async);
    }

    @Override
    public Player getActor() {
        return getPlayer();
    }
}
