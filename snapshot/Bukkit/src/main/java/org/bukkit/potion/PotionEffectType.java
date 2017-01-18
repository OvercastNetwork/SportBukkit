package org.bukkit.potion;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.registry.Key;
import org.bukkit.registry.Registerable;

/**
 * Represents a type of potion and its effect on an entity.
 */
public abstract class PotionEffectType implements Registerable {
    /**
     * Increases movement speed.
     */
    public static final PotionEffectType SPEED = new PotionEffectTypeWrapper("speed");

    /**
     * Decreases movement speed.
     */
    public static final PotionEffectType SLOW = new PotionEffectTypeWrapper("slowness");

    /**
     * Increases dig speed.
     */
    public static final PotionEffectType FAST_DIGGING = new PotionEffectTypeWrapper("haste");

    /**
     * Decreases dig speed.
     */
    public static final PotionEffectType SLOW_DIGGING = new PotionEffectTypeWrapper("mining_fatigue");

    /**
     * Increases damage dealt.
     */
    public static final PotionEffectType INCREASE_DAMAGE = new PotionEffectTypeWrapper("strength");

    /**
     * Heals an entity.
     */
    public static final PotionEffectType HEAL = new PotionEffectTypeWrapper("instant_health");

    /**
     * Hurts an entity.
     */
    public static final PotionEffectType HARM = new PotionEffectTypeWrapper("instant_damage");

    /**
     * Increases jump height.
     */
    public static final PotionEffectType JUMP = new PotionEffectTypeWrapper("jump_boost");

    /**
     * Warps vision on the client.
     */
    public static final PotionEffectType CONFUSION = new PotionEffectTypeWrapper("nausea");

    /**
     * Regenerates health.
     */
    public static final PotionEffectType REGENERATION = new PotionEffectTypeWrapper("regeneration");

    /**
     * Decreases damage dealt to an entity.
     */
    public static final PotionEffectType DAMAGE_RESISTANCE = new PotionEffectTypeWrapper("resistance");

    /**
     * Stops fire damage.
     */
    public static final PotionEffectType FIRE_RESISTANCE = new PotionEffectTypeWrapper("fire_resistance");

    /**
     * Allows breathing underwater.
     */
    public static final PotionEffectType WATER_BREATHING = new PotionEffectTypeWrapper("water_breathing");

    /**
     * Grants invisibility.
     */
    public static final PotionEffectType INVISIBILITY = new PotionEffectTypeWrapper("invisibility");

    /**
     * Blinds an entity.
     */
    public static final PotionEffectType BLINDNESS = new PotionEffectTypeWrapper("blindness");

    /**
     * Allows an entity to see in the dark.
     */
    public static final PotionEffectType NIGHT_VISION = new PotionEffectTypeWrapper("night_vision");

    /**
     * Increases hunger.
     */
    public static final PotionEffectType HUNGER = new PotionEffectTypeWrapper("hunger");

    /**
     * Decreases damage dealt by an entity.
     */
    public static final PotionEffectType WEAKNESS = new PotionEffectTypeWrapper("weakness");

    /**
     * Deals damage to an entity over time.
     */
    public static final PotionEffectType POISON = new PotionEffectTypeWrapper("poison");

    /**
     * Deals damage to an entity over time and gives the health to the
     * shooter.
     */
    public static final PotionEffectType WITHER = new PotionEffectTypeWrapper("wither");

    /**
     * Increases the maximum health of an entity.
     */
    public static final PotionEffectType HEALTH_BOOST = new PotionEffectTypeWrapper("health_boost");

    /**
     * Increases the maximum health of an entity with health that cannot be
     * regenerated, but is refilled every 30 seconds.
     */
    public static final PotionEffectType ABSORPTION = new PotionEffectTypeWrapper("absorption");

    /**
     * Increases the food level of an entity each tick.
     */
    public static final PotionEffectType SATURATION = new PotionEffectTypeWrapper("saturation");

    /**
     * Outlines the entity so that it can be seen from afar.
     */
    public static final PotionEffectType GLOWING = new PotionEffectTypeWrapper("glowing");

    /**
     * Causes the entity to float into the air.
     */
    public static final PotionEffectType LEVITATION = new PotionEffectTypeWrapper("levitation");

    /**
     * Loot table luck.
     */
    public static final PotionEffectType LUCK = new PotionEffectTypeWrapper("luck");

    /**
     * Loot table unluck.
     */
    public static final PotionEffectType UNLUCK = new PotionEffectTypeWrapper("unluck");

    /**
     * Creates a PotionEffect from this PotionEffectType, applying duration
     * modifiers and checks.
     *
     * @param duration time in ticks
     * @param amplifier the effect's amplifier
     * @return a resulting potion effect
     */
    public PotionEffect createEffect(int duration, int amplifier) {
        return new PotionEffect(this, isInstant() ? 1 : (int) (duration * getDurationModifier()), amplifier);
    }

    /**
     * Returns the duration modifier applied to effects of this type.
     *
     * @return duration modifier
     */
    public abstract double getDurationModifier();

    /**
     * Returns the unique ID of this type.
     *
     * @return Unique ID
     * @deprecated Magic value
     */
    @Deprecated
    public abstract int getId();

    /**
     * Returns the legacy Bukkit name of this effect type.
     *
     * @return The name of this effect type
     * @deprecated Use {@link #key()}
     */
    @Deprecated
    public abstract String getName();

    /**
     * Returns whether the effect of this type happens once, immediately.
     *
     * @return whether this type is normally instant
     */
    public abstract boolean isInstant();

    /**
     * Returns the color of this effect type.
     *
     * @return the color
     */
    public abstract Color getColor();

    @Override
    public boolean equals(Object obj) {
        return this == obj || (
            obj instanceof PotionEffectType &&
            key().equals(((PotionEffectType) obj).key())
        );
    }

    @Override
    public int hashCode() {
        return key().hashCode();
    }

    @Override
    public String toString() {
        return "PotionEffectType[" + key() + ", " + getName() + "]";
    }

    /**
     * Gets the effect type specified by the unique id.
     *
     * @param id Unique ID to fetch
     * @return Resulting type, or null if not found.
     * @deprecated Magic value
     */
    @Deprecated
    public static PotionEffectType getById(int id) {
        return Bukkit.potionEffectRegistry().forLegacyId(id);
    }

    /**
     * Gets the effect type specified by the given legacy Bukkit name.
     *
     * @param name Name of PotionEffectType to fetch
     * @return Resulting PotionEffectType, or null if not found.
     * @deprecated Use {@link PotionEffectRegistry#get(Key)}
     */
    public static PotionEffectType getByName(String name) {
        return Bukkit.potionEffectRegistry().forLegacyName(name);
    }

    /**
     * Legacy method, not to be called under any circumstances
     */
    @Deprecated
    public static void registerPotionEffectType(PotionEffectType type) {
        throw new UnsupportedOperationException();
    }

    /**
     * Legacy method, not to be called under any circumstances
     */
    @Deprecated
    public static void stopAcceptingRegistrations() {
    }

    /**
     * Returns an array of all the registered {@link PotionEffectType}s.
     * This array is not necessarily in any particular order and may contain null.
     *
     * @return Array of types.
     * @deprecated Use {@link PotionEffectRegistry#iterator()}
     */
    @Deprecated
    public static PotionEffectType[] values() {
        final List<PotionEffectType> list = new ArrayList<PotionEffectType>();
        for(PotionEffectType type : Bukkit.potionEffectRegistry()) {
            list.add(type);
        }
        return list.toArray(new PotionEffectType[list.size()]);
    }
}
