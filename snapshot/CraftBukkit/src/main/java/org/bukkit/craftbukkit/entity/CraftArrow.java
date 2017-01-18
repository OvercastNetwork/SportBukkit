package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityArrow;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;

public class CraftArrow extends AbstractProjectile implements Arrow {

    public CraftArrow(CraftServer server, EntityArrow entity) {
        super(server, entity);
    }

    @Override
    public double getDamage() {
        return getHandle().getDamage();
    }

    @Override
    public void setDamage(double damage) {
        getHandle().setDamage(damage);
    }

    public void setKnockbackStrength(int knockbackStrength) {
        Validate.isTrue(knockbackStrength >= 0, "Knockback cannot be negative");
        getHandle().setKnockbackStrength(knockbackStrength);
    }

    public int getKnockbackStrength() {
        return getHandle().knockbackStrength;
    }

    public boolean isCritical() {
        return getHandle().isCritical();
    }

    public void setCritical(boolean critical) {
        getHandle().setCritical(critical);
    }

    public ProjectileSource getShooter() {
        return getHandle().projectileSource;   
    }

    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof LivingEntity) {
            getHandle().shooter = ((CraftLivingEntity) shooter).getHandle();
        } else {
            getHandle().shooter = null;
        }
        getHandle().projectileSource = shooter;
    }

    @Override
    public PickupRule getPickupRule() {
        return convertPickupRule(getHandle().fromPlayer);
    }

    @Override
    public void setPickupRule(PickupRule rule) {
        getHandle().fromPlayer = convertPickupRule(rule);
    }

    public static PickupRule convertPickupRule(EntityArrow.PickupStatus nms) {
        switch(nms) {
            case DISALLOWED: return PickupRule.DISALLOWED;
            case ALLOWED: return PickupRule.ALLOWED;
            case CREATIVE_ONLY: return PickupRule.CREATIVE_ONLY;
            default: throw new IllegalStateException();
        }
    }

    public static EntityArrow.PickupStatus convertPickupRule(PickupRule bukkit) {
        switch(bukkit) {
            case DISALLOWED: return EntityArrow.PickupStatus.DISALLOWED;
            case ALLOWED: return EntityArrow.PickupStatus.ALLOWED;
            case CREATIVE_ONLY: return EntityArrow.PickupStatus.CREATIVE_ONLY;
            default: throw new IllegalStateException();
        }
    }

    @Override
    public EntityArrow getHandle() {
        return (EntityArrow) entity;
    }

    @Override
    public String toString() {
        return "CraftArrow";
    }

    public EntityType getType() {
        return EntityType.ARROW;
    }

    @Deprecated
    public LivingEntity _INVALID_getShooter() {
        if (getHandle().shooter == null) {
            return null;
        }
        return (LivingEntity) getHandle().shooter.getBukkitEntity();
    }

    @Deprecated
    public void _INVALID_setShooter(LivingEntity shooter) {
        getHandle().shooter = ((CraftLivingEntity) shooter).getHandle();
    }
}
