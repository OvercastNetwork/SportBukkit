package org.bukkit.potion;

import org.bukkit.Bukkit;
import org.bukkit.registry.Key;

/**
 * This enum reflects and matches each potion state that can be obtained from
 * the Creative mode inventory
 */
public enum PotionType {
    UNCRAFTABLE("empty", null, false, false),
    WATER("water", null, false, false),
    MUNDANE("mundane", null, false, false),
    THICK("thick", null, false, false),
    AWKWARD("awkward", null, false, false),
    NIGHT_VISION("night_vision", PotionEffectType.NIGHT_VISION, false, true),
    INVISIBILITY("invisibility", PotionEffectType.INVISIBILITY, false, true),
    JUMP("leaping", PotionEffectType.JUMP, true, true),
    FIRE_RESISTANCE("fire_resistance", PotionEffectType.FIRE_RESISTANCE, false, true),
    SPEED("swiftness", PotionEffectType.SPEED, true, true),
    SLOWNESS("slowness", PotionEffectType.SLOW, false, true),
    WATER_BREATHING("water_breathing", PotionEffectType.WATER_BREATHING, false, true),
    INSTANT_HEAL("healing", PotionEffectType.HEAL, true, false),
    INSTANT_DAMAGE("harming", PotionEffectType.HARM, true, false),
    POISON("poison", PotionEffectType.POISON, true, true),
    REGEN("regeneration", PotionEffectType.REGENERATION, true, true),
    STRENGTH("strength", PotionEffectType.INCREASE_DAMAGE, true, true),
    WEAKNESS("weakness", PotionEffectType.WEAKNESS, false, true),
    LUCK("luck", PotionEffectType.LUCK, false, false);

    private static final String LONG_PREFIX = "long_";
    private static final String STRONG_PREFIX = "strong_";

    private final PotionEffectType effect;
    private final boolean upgradeable;
    private final boolean extendable;
    private final String id;

    PotionType(String id, PotionEffectType effect, boolean upgradeable, boolean extendable) {
        this.id = id;
        this.effect = effect;
        this.upgradeable = upgradeable;
        this.extendable = extendable;
    }

    public Key baseKey() {
        return Bukkit.key(id);
    }

    public Key longKey() {
        return Bukkit.key(LONG_PREFIX + id);
    }

    public Key strongKey() {
        return Bukkit.key(STRONG_PREFIX + id);
    }

    public PotionEffectType getEffectType() {
        return effect;
    }

    public boolean isInstant() {
        return effect != null && effect.isInstant();
    }

    /**
     * Checks if the potion type has an upgraded state.
     * This refers to whether or not the potion type can be Tier 2,
     * such as Potion of Fire Resistance II.
     * 
     * @return true if the potion type can be upgraded;
     */
    public boolean isUpgradeable() {
        return upgradeable;
    }

    /**
     * Checks if the potion type has an extended state.
     * This refers to the extended duration potions
     * 
     * @return true if the potion type can be extended
     */
    public boolean isExtendable() {
        return extendable;
    }

    /**
     * @deprecated Non-functional
     */
    @Deprecated
    public int getDamageValue() {
        return this.ordinal();
    }

    public int getMaxLevel() {
        return upgradeable ? 2 : 1;
    }

    /**
     * @deprecated Non-functional
     */
    @Deprecated
    public static PotionType getByDamageValue(int damage) {
        return null;
    }

    /**
     * @deprecated Misleading
     */
    @Deprecated
    public static PotionType getByEffect(PotionEffectType effectType) {
        if (effectType == null)
            return WATER;
        for (PotionType type : PotionType.values()) {
            if (effectType.equals(type.effect))
                return type;
        }
        return null;
    }
}
