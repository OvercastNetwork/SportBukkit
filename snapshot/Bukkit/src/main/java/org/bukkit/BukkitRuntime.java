package org.bukkit;

import com.google.inject.Injector;
import org.bukkit.block.BlockFactory;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionBrewRegistry;
import org.bukkit.potion.PotionEffectRegistry;
import org.bukkit.registry.Key;
import org.bukkit.geometry.VectorFactory;
import tc.oc.minecraft.api.text.TextRenderContext;

public interface BukkitRuntime {

    /**
     * Return the global {@link Injector}.
     *
     * Note that direct injector use is considered bad form,
     * and is only provided to assist in migrating legacy code.
     * Nice code should @Inject its dependencies.
     */
    Injector injector();

    Key key(String prefix, String id);

    Key key(String key);

    VectorFactory vectors();

    BlockFactory blocks();

    /**
     * Gets the instance of the item factory (for {@link ItemMeta}).
     *
     * @return the item factory
     * @see ItemFactory
     */
    ItemFactory getItemFactory();

    PotionBrewRegistry potionRegistry();

    PotionEffectRegistry potionEffectRegistry();

    default TextRenderContext textRenderContext() {
        return (text, viewer) -> text;
    }
}
