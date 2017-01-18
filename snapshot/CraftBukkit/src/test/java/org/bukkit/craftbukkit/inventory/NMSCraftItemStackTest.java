package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.BlockCloth;
import net.minecraft.server.Blocks;
import net.minecraft.server.Enchantments;
import net.minecraft.server.EnumColor;
import net.minecraft.server.Items;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class NMSCraftItemStackTest extends AbstractTestingBase {

    @Test
    public void testCloneEnchantedItem() throws Exception {
        net.minecraft.server.ItemStack nmsItemStack = new net.minecraft.server.ItemStack(net.minecraft.server.Items.POTION);
        nmsItemStack.addEnchantment(Enchantments.DAMAGE_ALL, 1);
        ItemStack itemStack = CraftItemStack.asCraftMirror(nmsItemStack);
        ItemStack clone = itemStack.clone();
        assertThat(clone.getType(), is(itemStack.getType()));
        assertThat(clone.getAmount(), is(itemStack.getAmount()));
        assertThat(clone.getDurability(), is(itemStack.getDurability()));
        assertThat(clone.getEnchantments(), is(itemStack.getEnchantments()));
        assertThat(clone.getTypeId(), is(itemStack.getTypeId()));
        assertThat(clone.getData(), is(itemStack.getData()));
        assertThat(clone, is(itemStack));
    }

    @Test
    public void testCloneNullItem() throws Exception {
        net.minecraft.server.ItemStack nmsItemStack = null;
        ItemStack itemStack = CraftItemStack.asCraftMirror(nmsItemStack);
        ItemStack clone = itemStack.clone();
        assertThat(clone, is(itemStack));
    }

    @Test
    public void testConversionPreservesItemData() throws Throwable {
        ItemStack bukkit = new Wool(DyeColor.PINK).toItemStack();
        net.minecraft.server.ItemStack nms = new net.minecraft.server.ItemStack(Blocks.WOOL, 1, Blocks.WOOL.getDropData(Blocks.WOOL.getBlockData().set(BlockCloth.COLOR, EnumColor.PINK)));

        assertEquals(bukkit, CraftItemStack.asBukkitCopy(nms));
        assertEquals(bukkit, CraftItemStack.asCraftMirror(nms));
        assertEquals(bukkit, CraftItemStack.asCraftCopy(bukkit));

        assertTrue(net.minecraft.server.ItemStack.equals(nms, CraftItemStack.asNMSCopy(bukkit)));
    }

    @Test
    public void testConversionPreservesDurability() throws Throwable {
        ItemStack bukkit = new ItemStack(Material.STONE_SWORD, 1, (short) 123);
        net.minecraft.server.ItemStack nms = new net.minecraft.server.ItemStack(Items.STONE_SWORD, 1, 123);

        assertEquals(bukkit, CraftItemStack.asBukkitCopy(nms));
        assertEquals(bukkit, CraftItemStack.asCraftMirror(nms));
        assertEquals(bukkit, CraftItemStack.asCraftCopy(bukkit));

        assertTrue(net.minecraft.server.ItemStack.equals(nms, CraftItemStack.asNMSCopy(bukkit)));
    }
}
