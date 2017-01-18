package org.bukkit.craftbukkit.potion;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.minecraft.server.MobEffect;
import net.minecraft.server.PotionRegistry;
import org.bukkit.craftbukkit.registry.CraftKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionBrew;
import org.bukkit.registry.Key;

public class CraftPotionBrew implements PotionBrew {

    private final PotionRegistry handle;

    public CraftPotionBrew(PotionRegistry handle) {
        this.handle = handle;
    }

    @Override
    public Key key() {
        return CraftKey.get(PotionRegistry.a.b(handle));
    }

    @Override
    public List<PotionEffect> effects() {
        return Lists.transform(handle.a(), new Function<MobEffect, PotionEffect>() {
            @Override
            public PotionEffect apply(MobEffect nms) {
                return CraftPotionUtils.toBukkit(nms);
            }
        });
    }
}
