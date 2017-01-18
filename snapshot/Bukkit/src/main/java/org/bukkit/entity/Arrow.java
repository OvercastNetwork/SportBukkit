package org.bukkit.entity;

/**
 * Represents an arrow.
 */
public interface Arrow extends Projectile {

    /**
     * Get the base damage value for this arrow. This value is affected
     * by the power enchantment of the bow that fired the arrow. Normal
     * arrows fired by a player from an unenchanted bow have a damage
     * value of 2.
     *
     * The actual damage delivered is derived from this value, but also
     * depends on the velocity of the arrow at the moment of impact,
     * and whether or not the arrow is critical.
     *
     * @see #setDamage(double)
     */
    double getDamage();

    /**
     * Set the base damage value for this arrow.
     *
     * @see #getDamage()
     */
    void setDamage(double damage);

    /**
     * Gets the knockback strength for an arrow, which is the
     * {@link org.bukkit.enchantments.Enchantment#KNOCKBACK KnockBack} level
     * of the bow that shot it.
     *
     * @return the knockback strength value
     */
    public int getKnockbackStrength();

    /**
     * Sets the knockback strength for an arrow.
     *
     * @param knockbackStrength the knockback strength value
     */
    public void setKnockbackStrength(int knockbackStrength);

    /**
     * Gets whether this arrow is critical.
     * <p>
     * Critical arrows have increased damage and cause particle effects.
     * <p>
     * Critical arrows generally occur when a player fully draws a bow before
     * firing.
     *
     * @return true if it is critical
     */
    public boolean isCritical();

    /**
     * Sets whether or not this arrow should be critical.
     *
     * @param critical whether or not it should be critical
     */
    public void setCritical(boolean critical);

    /**
     * Which players can pickup this arrow as an item?
     *
     * This is generally true only if the arrow was NOT fired from a bow with the infinity enchantment.
     */
    PickupRule getPickupRule();

    /**
     * Set the rule for which players can pickup this arrow as an item.
     */
    void setPickupRule(PickupRule rule);

    enum PickupRule {
        DISALLOWED, ALLOWED, CREATIVE_ONLY
    }
}
