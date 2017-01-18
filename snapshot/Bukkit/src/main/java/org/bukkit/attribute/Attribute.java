package org.bukkit.attribute;

import java.util.HashMap;
import java.util.Map;

/**
 * Types of attributes which may be present on an {@link Attributable}.
 */
public enum Attribute {

    /**
     * Maximum health of an Entity.
     */
    GENERIC_MAX_HEALTH("generic.maxHealth"),
    /**
     * Range at which an Entity will follow others.
     */
    GENERIC_FOLLOW_RANGE("generic.followRange"),
    /**
     * Resistance of an Entity to knockback.
     */
    GENERIC_KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
    /**
     * Movement speed of an Entity.
     */
    GENERIC_MOVEMENT_SPEED("generic.movementSpeed"),
    /**
     * Attack damage of an Entity.
     */
    GENERIC_ATTACK_DAMAGE("generic.attackDamage"),
    /**
     * Attack speed of an Entity.
     */
    GENERIC_ATTACK_SPEED("generic.attackSpeed"),
    /**
     * Armor bonus of an Entity.
     */
    GENERIC_ARMOR("generic.armor"),
    /**
     * Armor bonus of an Entity.
     */
    GENERIC_ARMOR_TOUGHNESS("generic.armorToughness"),
    /**
     * Luck bonus of an Entity.
     */
    GENERIC_LUCK("generic.luck"),
    /**
     * Strength with which a horse will jump.
     */
    HORSE_JUMP_STRENGTH("horse.jumpStrength"),
    /**
     * Chance of a zombie to spawn reinforcements.
     */
    ZOMBIE_SPAWN_REINFORCEMENTS("zombie.spawnReinforcements"),

    ARROW_ACCURACY("sportbukkit.arrowAccuracy"),

    ARROW_VELOCITY_TRANSFER("sportbukkit.arrowVelocityTransfer"),

    SHIELD_STRENGTH("sportbukkit.shieldStrength");

    private final String name;

    Attribute(String name) {
        this.name = name;
    }

    /**
     * @return the external name of this attribute
     */
    public String getName() {
        return name;
    }

    private static final Map<String, Attribute> byName = new HashMap<String, Attribute>();

    static {
        for(Attribute attribute : values()) {
            byName.put(attribute.getName(), attribute);
        }
    }

    public static Attribute byName(String name) {
        return byName.get(name);
    }
}
