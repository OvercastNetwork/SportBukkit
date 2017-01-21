package org.bukkit.craftbukkit.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.server.IHopper;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.InventoryEnderChest;
import net.minecraft.server.InventoryMerchant;
import net.minecraft.server.LocaleI18n;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.TileEntityBeacon;
import net.minecraft.server.TileEntityBrewingStand;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityDropper;
import net.minecraft.server.TileEntityFurnace;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import tc.oc.collection.ListUtils;

public class CraftInventory implements Inventory {
    protected final IInventory inventory;
    private final List<ItemStack> contents;

    public CraftInventory(IInventory inventory) {
        this.inventory = inventory;
        List<net.minecraft.server.ItemStack> contents = inventory.getContents();
        if(inventory.getSize() < contents.size()) {
            // Some inventories have contents bigger than their size e.g. chest minecart
            contents = contents.subList(0, inventory.getSize());
        }
        this.contents = Lists.transform(contents, CraftItemStack::asCraftMirror);
    }

    public IInventory getInventory() {
        return inventory;
    }

    public int getSize() {
        return getInventory().getSize();
    }

    public String getName() {
        return getInventory().hasCustomName() ? getInventory().getName() : LocaleI18n.get(getInventory().getName());
    }

    @Override
    public boolean hasCustomName() {
        return getInventory().hasCustomName();
    }

    @Override
    public BaseComponent getLocalizedName() {
        if(getInventory().hasCustomName()) {
            return new TextComponent(getInventory().getName());
        } else {
            return new TranslatableComponent(getInventory().getName());
        }
    }

    public ItemStack getItem(int index) {
        net.minecraft.server.ItemStack item = getInventory().getItem(index);
        return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
    }

    @Override
    public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
        setContents(items);
    }

    @Override
    public List<ItemStack> contents() {
        return contents;
    }

    public void setContents(List<ItemStack> items) {
        if (getSize() < items.size()) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getSize() + " or less");
        }

        for (int i = 0; i < getSize(); i++) {
            if (i >= items.size()) {
                setItem(i, null);
            } else {
                setItem(i, items.get(i));
            }
        }
    }

    public void setItem(int index, ItemStack item) {
        getInventory().setItem(index, CraftItemStack.asNMSCopy(item));
    }

    public boolean contains(int materialId) {
        for (ItemStack item : storage()) {
            if (item != null && item.getTypeId() == materialId) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return contains(material.getId());
    }

    public boolean contains(ItemStack item) {
        return item != null && storage().contains(item);
    }

    public boolean contains(int materialId, int amount) {
        if (amount <= 0) {
            return true;
        }
        for (ItemStack item : storage()) {
            if (item != null && item.getTypeId() == materialId) {
                if ((amount -= item.getAmount()) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(Material material, int amount) {
        Validate.notNull(material, "Material cannot be null");
        return contains(material.getId(), amount);
    }

    public boolean contains(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (ItemStack i : storage()) {
            if (item.equals(i) && --amount <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAtLeast(ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (ItemStack i : storage()) {
            if (item.isSimilar(i) && (amount -= i.getAmount()) <= 0) {
                return true;
            }
        }
        return false;
    }

    public HashMap<Integer, ItemStack> all(int materialId) {
        HashMap<Integer, ItemStack> slots = new HashMap<Integer, ItemStack>();

        List<ItemStack> inventory = storage();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack item = inventory.get(i);
            if (item != null && item.getTypeId() == materialId) {
                slots.put(i, item);
            }
        }
        return slots;
    }

    public HashMap<Integer, ItemStack> all(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return all(material.getId());
    }

    public HashMap<Integer, ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> slots = new HashMap<Integer, ItemStack>();
        if (item != null) {
            List<ItemStack> inventory = storage();
            for (int i = 0; i < inventory.size(); i++) {
                if (item.equals(inventory.get(i))) {
                    slots.put(i, inventory.get(i));
                }
            }
        }
        return slots;
    }

    public int first(int materialId) {
        return ListUtils.indexOf(storage(), item -> item != null && item.getTypeId() == materialId);
    }

    public int first(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return first(material.getId());
    }

    public int first(ItemStack item) {
        return first(item, true);
    }

    private int first(ItemStack item, boolean withAmount) {
        if (item == null) {
            return -1;
        }
        return ListUtils.indexOf(storage(), i -> i != null && withAmount ? item.equals(i) : item.isSimilar(i));
    }

    public int firstEmpty() {
        return ListUtils.indexOf(storage(), Objects::isNull);
    }

    public int firstPartial(int materialId) {
        return ListUtils.indexOf(storage(), item ->
            item != null &&
            item.getTypeId() == materialId &&
            item.getAmount() < item.getMaxStackSize()
        );
    }

    public int firstPartial(Material material) {
        Validate.notNull(material, "Material cannot be null");
        return firstPartial(material.getId());
    }

    protected int firstPartial(ItemStack item) {
        final ItemStack filteredItem = CraftItemStack.asCraftCopy(item);
        return ListUtils.indexOf(storage(), cItem ->
            cItem != null &&
            cItem.getAmount() < cItem.getMaxStackSize() &&
            cItem.isSimilar(filteredItem)
        );
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        Validate.noNullElements(items, "Item cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        /* TODO: some optimization
         *  - Create a 'firstPartial' with a 'fromIndex'
         *  - Record the lastPartial per Material
         *  - Cache firstEmpty result
         */

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            while (true) {
                // Do we already have a stack of it?
                int firstPartial = firstPartial(item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > getMaxItemStack()) {
                            CraftItemStack stack = CraftItemStack.asCraftCopy(item);
                            stack.setAmount(getMaxItemStack());
                            setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - getMaxItemStack());
                        } else {
                            // Just store it
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    ItemStack partialItem = getItem(firstPartial);

                    int amount = item.getAmount();
                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        // To make sure the packet is sent to the client
                        setItem(firstPartial, partialItem);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    // To make sure the packet is sent to the client
                    setItem(firstPartial, partialItem);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        Validate.notNull(items, "Items cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        // TODO: optimization

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            int toDelete = item.getAmount();

            while (true) {
                int first = first(item, false);

                // Drat! we don't have this type in the inventory
                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                } else {
                    ItemStack itemStack = getItem(first);
                    int amount = itemStack.getAmount();

                    if (amount <= toDelete) {
                        toDelete -= amount;
                        // clear the slot, all used up
                        clear(first);
                    } else {
                        // split the stack and store
                        itemStack.setAmount(amount - toDelete);
                        setItem(first, itemStack);
                        toDelete = 0;
                    }
                }

                // Bail when done
                if (toDelete <= 0) {
                    break;
                }
            }
        }
        return leftover;
    }

    private int getMaxItemStack() {
        return getInventory().getMaxStackSize();
    }

    @Override
    public void removeIf(Predicate<? super ItemStack> filter) {
        final List<ItemStack> items = storage();
        for(int i = 0; i < items.size(); i++) {
            final ItemStack item = items.get(i);
            if(item != null && filter.test(item)) {
                clear(i);
            }
        }
    }

    public void remove(int materialId) {
        removeIf(item -> item.getTypeId() == materialId);
    }

    public void remove(Material material) {
        Validate.notNull(material, "Material cannot be null");
        remove(material.getId());
    }

    public void remove(ItemStack item) {
        removeIf(i -> i.equals(item));
    }

    public void clear(int index) {
        setItem(index, null);
    }

    public void clear() {
        for (int i = 0; i < getSize(); i++) {
            clear(i);
        }
    }

    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator(this);
    }

    public ListIterator<ItemStack> iterator(int index) {
        if (index < 0) {
            index += getSize() + 1; // ie, with -1, previous() will return the last element
        }
        return new InventoryIterator(this, index);
    }

    public List<HumanEntity> getViewers() {
        return this.inventory.getViewers();
    }

    public String getTitle() {
        return getName();
    }

    public InventoryType getType() {
        // Thanks to Droppers extending Dispensers, order is important.
        if (inventory instanceof InventoryCrafting) {
            return inventory.getSize() >= 9 ? InventoryType.WORKBENCH : InventoryType.CRAFTING;
        } else if (inventory instanceof PlayerInventory) {
            return InventoryType.PLAYER;
        } else if (inventory instanceof TileEntityDropper) {
            return InventoryType.DROPPER;
        } else if (inventory instanceof TileEntityDispenser) {
            return InventoryType.DISPENSER;
        } else if (inventory instanceof TileEntityFurnace) {
            return InventoryType.FURNACE;
        } else if (this instanceof CraftInventoryEnchanting) {
           return InventoryType.ENCHANTING;
        } else if (inventory instanceof TileEntityBrewingStand) {
            return InventoryType.BREWING;
        } else if (inventory instanceof CraftInventoryCustom.MinecraftInventory) {
            return ((CraftInventoryCustom.MinecraftInventory) inventory).getType();
        } else if (inventory instanceof InventoryEnderChest) {
            return InventoryType.ENDER_CHEST;
        } else if (inventory instanceof InventoryMerchant) {
            return InventoryType.MERCHANT;
        } else if (inventory instanceof TileEntityBeacon) {
            return InventoryType.BEACON;
        } else if (this instanceof CraftInventoryAnvil) {
           return InventoryType.ANVIL;
        } else if (inventory instanceof IHopper) {
            return InventoryType.HOPPER;
        } else {
            return InventoryType.CHEST;
        }
    }

    public InventoryHolder getHolder() {
        return inventory.getOwner();
    }

    @Override
    public World getWorld() {
        return getLocation().getWorld();
    }

    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    public void setMaxStackSize(int size) {
        inventory.setMaxStackSize(size);
    }

    @Override
    public int hashCode() {
        return inventory.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CraftInventory && ((CraftInventory) obj).inventory.equals(this.inventory);
    }

    @Override
    public Location getLocation() {
        return inventory.getLocation();
    }
}
