package org.bukkit.potion;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.registry.Key;

public class PotionEffectTypeWrapper extends PotionEffectType {

    private final String id;

    protected PotionEffectTypeWrapper(String id) {
        this.id = id;
    }

    @Override
    public double getDurationModifier() {
        return getType().getDurationModifier();
    }

    @Override
    public Key key() {
        return Bukkit.key(id);
    }

    @Override
    public int getId() {
        return getType().getId();
    }

    @Override
    public String getName() {
        return getType().getName();
    }

    /**
     * Get the potion type bound to this wrapper.
     *
     * @return The potion effect type
     */
    public PotionEffectType getType() {
        return Bukkit.potionEffectRegistry().get(key());
    }

    @Override
    public boolean isInstant() {
        return getType().isInstant();
    }

    @Override
    public Color getColor() {
        return getType().getColor();
    }
}
