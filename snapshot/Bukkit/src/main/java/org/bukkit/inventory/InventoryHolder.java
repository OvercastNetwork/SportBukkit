package org.bukkit.inventory;

import org.bukkit.Physical;

public interface InventoryHolder extends Physical {

    /**
     * Get the object's inventory.
     *
     * @return The inventory.
     */
    public Inventory getInventory();
}
