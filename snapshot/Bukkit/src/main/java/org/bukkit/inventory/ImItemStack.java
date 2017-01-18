package org.bukkit.inventory;

import java.util.Map;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ImItemStack extends ItemStack {

    public static ImItemStack of(Material material) {
        return of(material, (short) 0);
    }

    public static ImItemStack of(Material material, int damage) {
        return of(material, damage, 1);
    }

    public static ImItemStack of(Material material, int damage, int amount) {
        return of(material, damage, amount, null);
    }

    public static ImItemStack of(Material material, int damage, int amount, @Nullable ItemMeta meta) {
        return new ImItemStack(material, damage, amount, meta);
    }

    public static ImItemStack of(MaterialData material) {
        return of(material, 1);
    }

    public static ImItemStack of(MaterialData material, int amount) {
        return of(material, amount, null);
    }

    public static ImItemStack of(MaterialData material, int amount, @Nullable ItemMeta meta) {
        return new ImItemStack(material, amount, meta);
    }

    public static ImItemStack copyOf(ItemStack stack) {
        return stack instanceof ImItemStack ? (ImItemStack) stack : new ImItemStack(stack);
    }

    ImItemStack(Material material, int damage, int amount, @Nullable ItemMeta meta) {
        super(material, checkAmount(material, amount), checkDamage(material, damage), null);
        if(meta != null) {
            super.setItemMeta(meta);
        }
    }

    ImItemStack(MaterialData material, int amount, @Nullable ItemMeta meta) {
        super(material.getItemType(), checkAmount(material.getItemType(), amount), (short) 0, null);
        super.setData(material);
        if(meta != null) {
            super.setItemMeta(meta);
        }
    }

    ImItemStack(ItemStack stack) {
        super(stack);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public ImItemStack immutableCopy() {
        return this;
    }

    @Override
    public ItemStack clone() {
        return new ItemStack(this);
    }

    private UnsupportedOperationException ex() {
        return new UnsupportedOperationException("This " + ItemStack.class.getSimpleName() + " is immutable");
    }

    @Override public void setType(Material type) { throw ex(); }
    @Override public void setTypeId(int type) { throw ex(); }
    @Override public void setAmount(int amount) { throw ex(); }
    @Override public void setData(MaterialData data) { throw ex(); }
    @Override public void setDurability(short durability) { throw ex(); }
    @Override public void addEnchantments(Map<Enchantment, Integer> enchantments) { throw ex(); }
    @Override public void addEnchantment(Enchantment ench, int level) { throw ex(); }
    @Override public void addUnsafeEnchantments(Map<Enchantment, Integer> enchantments) { throw ex(); }
    @Override public void addUnsafeEnchantment(Enchantment ench, int level) { throw ex(); }
    @Override public int removeEnchantment(Enchantment ench) { throw ex(); }
    @Override public boolean setItemMeta(ItemMeta itemMeta) { throw ex(); }
}
