package org.bukkit;

import com.google.inject.Provides;
import org.bukkit.block.BlockFactory;
import org.bukkit.geometry.VectorFactory;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.potion.PotionBrewRegistry;
import org.bukkit.potion.PotionEffectRegistry;
import tc.oc.inject.SingletonModule;

/**
 * Bindings for global things that would be shared between servers,
 * if there were multiple servers.
 */
public class BukkitModule extends SingletonModule {

    @Override
    protected void configure() {}

    @Provides
    VectorFactory vectorFactory(BukkitRuntime bukkit) {
        return bukkit.vectors();
    }

    @Provides
    BlockFactory blockFactory(BukkitRuntime bukkit) {
        return bukkit.blocks();
    }

    @Provides
    ItemFactory itemFactory(BukkitRuntime bukkit) {
        return bukkit.getItemFactory();
    }

    @Provides
    PotionEffectRegistry potionEffectRegistry(BukkitRuntime bukkit) {
        return bukkit.potionEffectRegistry();
    }

    @Provides
    PotionBrewRegistry potionBrewRegistry(BukkitRuntime bukkit) {
        return bukkit.potionRegistry();
    }
}
