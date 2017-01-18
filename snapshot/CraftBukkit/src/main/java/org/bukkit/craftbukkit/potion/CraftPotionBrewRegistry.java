package org.bukkit.craftbukkit.potion;

import net.minecraft.server.PotionRegistry;
import org.bukkit.craftbukkit.registry.CraftKey;
import org.bukkit.craftbukkit.registry.CraftRegistry;
import org.bukkit.potion.PotionBrew;
import org.bukkit.potion.PotionBrewRegistry;

public class CraftPotionBrewRegistry extends CraftRegistry<PotionBrew, PotionRegistry> implements PotionBrewRegistry {

    public CraftPotionBrewRegistry() {
        super(PotionBrew.class, PotionRegistry.a);
    }

    @Override
    protected PotionBrew createBukkit(PotionRegistry nms) {
        return new CraftPotionBrew(nms);
    }

    @Override
    public PotionBrew getFallback() {
        return need(CraftKey.get("minecraft:empty"));
    }
}
