package org.bukkit.event.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Called when an entity is dispensed from a block.
 */
public class BlockDispenseEntityEvent extends BlockDispenseEvent implements Cancellable {
    private final Entity entity;
    private Location location;

    public BlockDispenseEntityEvent(final Block block, final ItemStack dispensed, final Entity entity) {
        this(block, dispensed, entity, entity.getLocation(), entity.getVelocity());
    }

    public BlockDispenseEntityEvent(final Block block, final ItemStack dispensed, final Entity entity, final Location location, final Vector velocity) {
        super(block, dispensed, velocity);
        this.entity = entity;
        this.location = location;
    }

    /**
     * Gets the entity that is being dispensed.
     *
     * @return An Entity for the item being dispensed
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Get the initial location of the dispensed entity
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Set the initial location of the dispensed entity
     */
    public void setLocation(Location location) {
        this.location = location;
    }
}
