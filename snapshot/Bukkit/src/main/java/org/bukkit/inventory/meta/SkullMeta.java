package org.bukkit.inventory.meta;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Skin;

/**
 * Represents a skull ({@link Material#SKULL_ITEM}) that can have an owner.
 */
public interface SkullMeta extends ItemMeta {

    /**
     * Gets the owner of the skull.
     *
     * @return the owner if the skull
     */
    String getOwner();

    /**
     * Checks to see if the skull has an owner.
     *
     * @return true if the skull has an owner
     */
    boolean hasOwner();

    /**
     * Sets the owner of the skull.
     * <p>
     * Plugins should check that hasOwner() returns true before calling this
     * plugin.
     *
     * @param owner the new owner of the skull
     * @return true if the owner was successfully set
     */
    boolean setOwner(String owner);

    /**
     * Set the owner and appearance of this skull. A skull with this data set
     * does not need to fetch anything remotely.
     *
     * @param name Username of the skull's owner, can be null (appears in item tooltip)
     * @param uuid UUID of the skull's owner
     * @param skin Skull owner's skin
     */
    void setOwner(String name, UUID uuid, Skin skin);

    /**
     * Clear any owner data in this skull
     */
    void clearOwner();

    SkullMeta clone();
}
