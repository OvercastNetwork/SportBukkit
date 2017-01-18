package org.bukkit;

import javax.annotation.Nullable;

import com.google.inject.Injector;
import net.minecraft.server.DispenserRegistry;
import org.bukkit.block.BlockFactory;
import org.bukkit.craftbukkit.block.CraftBlockFactory;
import org.bukkit.craftbukkit.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.potion.CraftPotionBrewRegistry;
import org.bukkit.craftbukkit.potion.CraftPotionEffectRegistry;
import org.bukkit.craftbukkit.registry.CraftKey;
import org.bukkit.craftbukkit.util.CraftVectorFactory;
import org.bukkit.potion.PotionBrewRegistry;
import org.bukkit.potion.PotionEffectRegistry;
import org.bukkit.registry.Key;
import org.bukkit.geometry.VectorFactory;

public class CraftBukkitRuntime implements BukkitRuntime {

    public static CraftBukkitRuntime load() {
        final BukkitRuntime runtime = Bukkit.getRuntime();
        if(runtime == null) {
            CraftBukkitRuntime craftBukkitRuntime = new CraftBukkitRuntime();
            Bukkit.setRuntime(craftBukkitRuntime);
            return craftBukkitRuntime;
        } else if(runtime instanceof CraftBukkitRuntime) {
            return (CraftBukkitRuntime) runtime;
        } else {
            throw new IllegalStateException("Bukkit runtime is already set to " + runtime.getClass().getName());
        }
    }

    protected @Nullable Injector injector;

    private final CraftBlockFactory blockFactory = new CraftBlockFactory();
    private final CraftVectorFactory vectorFactory = new CraftVectorFactory();

    private final PotionBrewRegistry potionBrewRegistry = new CraftPotionBrewRegistry();
    private final PotionEffectRegistry potionEffectRegistry = new CraftPotionEffectRegistry();

    public CraftBukkitRuntime() {
        DispenserRegistry.c();
    }

    @Override
    public Injector injector() {
        if(injector == null) {
            throw new IllegalStateException("Injector has not been created yet");
        }
        return injector;
    }

    @Override
    public Key key(String prefix, String id) {
        return CraftKey.get(prefix, id);
    }

    @Override
    public Key key(String key) {
        return CraftKey.get(key);
    }

    @Override
    public BlockFactory blocks() {
        return blockFactory;
    }

    @Override
    public VectorFactory vectors() {
        return vectorFactory;
    }

    @Override
    public CraftItemFactory getItemFactory() {
        return CraftItemFactory.instance();
    }

    @Override
    public PotionBrewRegistry potionRegistry() {
        return potionBrewRegistry;
    }

    @Override
    public PotionEffectRegistry potionEffectRegistry() {
        return potionEffectRegistry;
    }
}
