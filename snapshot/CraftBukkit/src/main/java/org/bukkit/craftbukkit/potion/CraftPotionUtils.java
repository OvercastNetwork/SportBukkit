package org.bukkit.craftbukkit.potion;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CraftPotionUtils {
    private CraftPotionUtils() {}

    public static PotionEffectType toBukkit(MobEffectList nms) {
        return PotionEffectType.getById(MobEffectList.getId(nms));
    }

    public static PotionEffect toBukkit(MobEffect effect) {
        return new PotionEffect(toBukkit(effect.getMobEffect()),
                                effect.getDuration(),
                                effect.getAmplifier(),
                                effect.isAmbient(),
                                effect.isShowParticles());
    }

    public static MobEffectList toNMS(PotionEffectType effect) {
        return MobEffectList.fromId(effect.getId());
    }

    public static MobEffect toNMS(PotionEffect effect) {
        return new MobEffect(toNMS(effect.getType()),
                             effect.getDuration(),
                             effect.getAmplifier(),
                             effect.isAmbient(),
                             effect.hasParticles());
    }

    public static MobEffect cloneWithDuration(MobEffect effect, int duration) {
        return new MobEffect(effect.getMobEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isShowParticles());
    }

    public static void extendDuration(MobEffect effect, int duration) {
        effect.a(cloneWithDuration(effect, duration));
    }
}
