package org.bukkit.craftbukkit.inventory;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ImItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.junit.Test;

import static org.junit.Assert.*;
import static tc.oc.test.Assert.*;

public class ImItemStackTest {

    @Test
    public void testCreationWithDamage() {
        ImItemStack stack = ImItemStack.of(Material.STONE_SWORD, 123, 45);
        assertTrue(stack.isImmutable());
        assertEquals(Material.STONE_SWORD, stack.getType());
        assertEquals(123, stack.getDurability());
        assertEquals(45, stack.getAmount());
    }

    @Test
    public void testCreationWithData() {
        Wool wool = new Wool(DyeColor.PINK);
        ImItemStack stack = ImItemStack.of(wool, 45);
        assertTrue(stack.isImmutable());
        assertEquals(wool, stack.getData());
        assertEquals(wool.getItemType(), stack.getType());
        assertEquals(45, stack.getAmount());
    }

    @Test
    public void testCreationWithBuilder() {
        Wool wool = new Wool(DyeColor.PINK);
        ImItemStack stack = wool.buildItemStack().amount(45).immutable();
        assertTrue(stack.isImmutable());
        assertEquals(wool.getItemType(), stack.getType());
        assertEquals(wool, stack.getData());
        assertEquals(45, stack.getAmount());
    }

    @Test
    public void testImmutableCopy() throws Throwable {
        ItemStack mutable = new ItemStack(Material.STONE_SWORD, 45, (short) 123);
        ItemStack immutable = mutable.immutableCopy();
        assertTrue(mutable.isMutable());
        assertTrue(immutable.isImmutable());
        assertNotSame(mutable, immutable);
        assertEquals(mutable, immutable);
    }

    @Test
    public void testCloneOfImmutableIsMutable() throws Throwable {
        ItemStack immutable = ImItemStack.of(Material.STONE_SWORD, 45, (short) 123);
        ItemStack mutable = immutable.clone();
        assertTrue(mutable.isMutable());
        assertTrue(immutable.isImmutable());
        assertNotSame(mutable, immutable);
        assertEquals(mutable, immutable);
    }

    @Test
    public void testImmutability() throws Throwable {
        ImItemStack stack = ImItemStack.of(Material.DIRT);
        assertFalse(stack.isMutable());
        assertTrue(stack.isImmutable());

        assertThrows(UnsupportedOperationException.class, () -> stack.setType(Material.STONE));
        assertThrows(UnsupportedOperationException.class, () -> stack.setTypeId(Material.STONE.getId()));
        assertThrows(UnsupportedOperationException.class, () -> stack.setDurability((short) 123));
        assertThrows(UnsupportedOperationException.class, () -> stack.setAmount(45));
        assertThrows(UnsupportedOperationException.class, () -> stack.setData(new Wool(DyeColor.PINK)));
        assertThrows(UnsupportedOperationException.class, () -> stack.setMaterial(new Wool(DyeColor.PINK)));
        assertThrows(UnsupportedOperationException.class, () -> stack.setItemMeta(stack.getItemMeta()));
    }

    @Test
    public void testDamageItemTypeCheck() throws Throwable {
        assertThrows(IllegalArgumentException.class, () -> ImItemStack.of(Material.DIRT, 123));
        assertThrows(IllegalArgumentException.class, () -> ItemStack.builder(Material.DIRT, 123));
    }

    @Test
    public void testDamageRangeCheck() throws Throwable {
        ImItemStack.of(Material.STONE_SWORD, Short.MIN_VALUE);
        ImItemStack.of(Material.STONE_SWORD, Short.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> ImItemStack.of(Material.STONE_SWORD, Short.MIN_VALUE - 1));
        assertThrows(IllegalArgumentException.class, () -> ImItemStack.of(Material.STONE_SWORD, Short.MAX_VALUE + 1));
    }

    @Test
    public void testAmountRangeCheck() throws Throwable {
        ImItemStack.of(Material.DIRT, 0, Byte.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> ImItemStack.of(Material.DIRT, 0, Byte.MAX_VALUE + 1));
    }
}
