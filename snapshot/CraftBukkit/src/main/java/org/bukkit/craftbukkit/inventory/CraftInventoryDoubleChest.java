package org.bukkit.craftbukkit.inventory;

import java.util.List;

import net.minecraft.server.ITileEntityContainer;
import net.minecraft.server.ITileInventory;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.InventoryLargeChest;
import org.bukkit.Location;

public class CraftInventoryDoubleChest extends CraftInventory implements DoubleChestInventory {
    private final CraftInventory left;
    private final CraftInventory right;

    public CraftInventoryDoubleChest(CraftInventory left, CraftInventory right) {
        super(new InventoryLargeChest("Large chest", (ITileInventory) left.getInventory(), (ITileInventory) right.getInventory()));
        this.left = left;
        this.right = right;
    }

    public CraftInventoryDoubleChest(InventoryLargeChest largeChest) {
        super(largeChest);
        if (largeChest.left instanceof InventoryLargeChest) {
            left = new CraftInventoryDoubleChest((InventoryLargeChest) largeChest.left);
        } else {
            left = new CraftInventory(largeChest.left);
        }
        if (largeChest.right instanceof InventoryLargeChest) {
            right = new CraftInventoryDoubleChest((InventoryLargeChest) largeChest.right);
        } else {
            right = new CraftInventory(largeChest.right);
        }
    }

    public Inventory getLeftSide() {
        return left;
    }

    public Inventory getRightSide() {
        return right;
    }

    @Override
    public void setContents(List<ItemStack> items) {
        if (getInventory().getContents().size() < items.size()) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getInventory().getContents().size() + " or less");
        }
        if(items.size() <= left.getSize()) {
            left.setContents(items);
            right.clear();
        } else {
            left.setContents(items.subList(0, left.getSize()));
            right.setContents(items.subList(left.getSize(), items.size()));
        }
    }

    @Override
    public DoubleChest getHolder() {
        return new DoubleChest(this);
    }

    @Override
    public Location getLocation() {
        return getLeftSide().getLocation().add(getRightSide().getLocation()).multiply(0.5);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DoubleChestInventory)) return false;
        DoubleChestInventory other = (DoubleChestInventory) obj;
        return this.left.equals(other.getLeftSide()) && this.right.equals(other.getRightSide());
    }

    @Override
    public int hashCode() {
        return 31 * this.left.hashCode() + this.right.hashCode();
    }
}
