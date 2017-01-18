package org.bukkit.craftbukkit.potion;

import net.minecraft.server.MobEffectList;
import org.bukkit.craftbukkit.registry.CraftRegistry;
import org.bukkit.potion.PotionEffectRegistry;
import org.bukkit.potion.PotionEffectType;

public class CraftPotionEffectRegistry extends CraftRegistry<PotionEffectType, MobEffectList> implements PotionEffectRegistry {

    public CraftPotionEffectRegistry() {
        super(PotionEffectType.class, MobEffectList.REGISTRY);
    }

    @Override
    protected PotionEffectType createBukkit(MobEffectList nms) {
        return new CraftPotionEffectType(nms);
    }

    @Override
    public PotionEffectType getFallback() {
        return null;
    }

    @Override
    public PotionEffectType forLegacyId(int legacyId) {
        final MobEffectList nms = MobEffectList.fromId(legacyId);
        return nms == null ? null : toBukkit(nms);
    }

    @Override
    public PotionEffectType forLegacyName(String legacyName) {
        for(PotionEffectType type : this) {
            if(legacyName.equals(type.getName())) return type;
        }
        return null;
    }
}
