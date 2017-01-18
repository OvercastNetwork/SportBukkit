package org.bukkit.potion;

import org.bukkit.registry.Registry;

public interface PotionEffectRegistry extends Registry<PotionEffectType> {
    @Deprecated
    PotionEffectType forLegacyId(int legacyId);

    @Deprecated
    PotionEffectType forLegacyName(String legacyName);
}
