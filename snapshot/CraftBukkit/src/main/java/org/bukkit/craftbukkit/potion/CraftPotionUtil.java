package org.bukkit.craftbukkit.potion;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.PotionData;
import org.bukkit.registry.Key;

public class CraftPotionUtil {

    // Matches the default returned by Mojang's registry
    public static final PotionData DEFAULT = new PotionData(PotionType.WATER, false, false);

    private static BiMap<Key, PotionData> map;
    private static BiMap<Key, PotionData> map() {
        if(map == null) {
            final ImmutableBiMap.Builder<Key, PotionData> builder = ImmutableBiMap.builder();
            for(PotionType type : PotionType.values()) {
                builder.put(type.baseKey(), new PotionData(type, false, false));
                if(type.isExtendable()) {
                    builder.put(type.longKey(), new PotionData(type, true, false));
                }
                if(type.isUpgradeable()) {
                    builder.put(type.strongKey(), new PotionData(type, false, true));
                }
            }
            map = builder.build();
        }
        return map;
    }

    public static String fromBukkit(PotionData data) {
        return Preconditions.checkNotNull(map().inverse().get(data), "Unknown potion type from data %s", data).toString();
    }

    public static PotionData toBukkit(String key) {
        if (key == null) return DEFAULT;
        final PotionData data = map().get(Bukkit.key(key));
        return data != null ? data : DEFAULT;
    }

    public static MobEffect fromBukkit(PotionEffect effect) {
        MobEffectList type = MobEffectList.fromId(effect.getType().getId());
        return new MobEffect(type, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles());
    }

    public static PotionEffect toBukkit(MobEffect effect) {
        PotionEffectType type = PotionEffectType.getById(MobEffectList.getId(effect.getMobEffect()));
        int amp = effect.getAmplifier();
        int duration = effect.getDuration();
        boolean ambient = effect.isAmbient();
        boolean particles = effect.isShowParticles();
        return new PotionEffect(type, duration, amp, ambient, particles);
    }

    public static boolean equals(MobEffectList mobEffect, PotionEffectType type) {
        PotionEffectType typeV = PotionEffectType.getById(MobEffectList.getId(mobEffect));
        return typeV.equals(type);
    }
}
