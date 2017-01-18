package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.IRecipe;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCrafting;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftInventoryCrafting extends CraftInventory implements CraftingInventory {
    private final IInventory resultInventory;

    public CraftInventoryCrafting(InventoryCrafting inventory, IInventory resultInventory) {
        super(inventory);
        this.resultInventory = resultInventory;
    }

    public IInventory getResultInventory() {
        return resultInventory;
    }

    public IInventory getMatrixInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return getResultInventory().getSize() + getMatrixInventory().getSize();
    }

    @Override
    public void setContents(List<ItemStack> items) {
        int resultLen = getResultInventory().getContents().size();
        int len = getMatrixInventory().getContents().size() + resultLen;
        if (len > items.size()) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + len + " or less");
        }
        setContents(items.get(0), items.subList(1, items.size()));
    }

    @Override
    public List<ItemStack> contents() {
        final List<ItemStack> items = new ArrayList<>(getSize());
        for(net.minecraft.server.ItemStack result : getResultInventory().getContents()) {
            items.add(CraftItemStack.asCraftMirror(result));
        }
        for(net.minecraft.server.ItemStack ingredient : getMatrixInventory().getContents()) {
            items.add(CraftItemStack.asCraftMirror(ingredient));
        }
        return items;
    }

    public void setContents(ItemStack result, List<ItemStack> contents) {
        setResult(result);
        setMatrix(contents);
    }

    @Override
    public CraftItemStack getItem(int index) {
        if (index < getResultInventory().getSize()) {
            net.minecraft.server.ItemStack item = getResultInventory().getItem(index);
            return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
        } else {
            net.minecraft.server.ItemStack item = getMatrixInventory().getItem(index - getResultInventory().getSize());
            return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (index < getResultInventory().getSize()) {
            getResultInventory().setItem(index, CraftItemStack.asNMSCopy(item));
        } else {
            getMatrixInventory().setItem((index - getResultInventory().getSize()), CraftItemStack.asNMSCopy(item));
        }
    }

    public ItemStack[] getMatrix() {
        List<net.minecraft.server.ItemStack> matrix = getMatrixInventory().getContents();
        ItemStack[] items = new ItemStack[matrix.size()];

        for (int i = 0; i < matrix.size(); i++) {
            items[i] = CraftItemStack.asCraftMirror(matrix.get(i));
        }

        return items;
    }

    public ItemStack getResult() {
        net.minecraft.server.ItemStack item = getResultInventory().getItem(0);
        if (!item.isEmpty()) return CraftItemStack.asCraftMirror(item);
        return null;
    }

    public void setMatrix(List<ItemStack> contents) {
        if (getMatrixInventory().getContents().size() > contents.size()) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getMatrixInventory().getContents().size() + " or less");
        }

        List<net.minecraft.server.ItemStack> mcItems = getMatrixInventory().getContents();

        for (int i = 0; i < mcItems.size(); i++) {
            if (i < contents.size()) {
                getMatrixInventory().setItem(i, CraftItemStack.asNMSCopy(contents.get(i)));
            } else {
                getMatrixInventory().setItem(i, net.minecraft.server.ItemStack.a);
            }
        }
    }

    public void setResult(ItemStack item) {
        List<net.minecraft.server.ItemStack> contents = getResultInventory().getContents();
        contents.set(0, CraftItemStack.asNMSCopy(item));
    }

    public Recipe getRecipe() {
        IRecipe recipe = ((InventoryCrafting)getInventory()).currentRecipe;
        return recipe == null ? null : recipe.toBukkitRecipe();
    }
}
