package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.EntityAction;

public abstract class EntityActionBase extends EntityEvent implements EntityAction {

    public EntityActionBase(Entity what) {
        super(what);
    }

    @Override
    public Entity getActor() {
        return getEntity();
    }
}
