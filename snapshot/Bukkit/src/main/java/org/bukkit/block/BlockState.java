package org.bukkit.block;

import org.bukkit.Chunk;
import org.bukkit.Locatable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.BlockReflection;
import org.bukkit.geometry.BlockRotation;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.Metadatable;
import org.bukkit.geometry.Vec3;

/**
 * Represents a captured state of a block, which will not change
 * automatically.
 * <p>
 * Unlike Block, which only one object can exist per coordinate, BlockState
 * can exist multiple times for any given Block. Note that another plugin may
 * change the state of the block and you will not know, or they may change the
 * block to another type entirely, causing your BlockState to become invalid.
 */
public interface BlockState extends Metadatable, Locatable {

    /**
     * Gets the block represented by this BlockState
     *
     * @return Block that this BlockState represents
     */
    Block getBlock();

    /**
     * Get the material of this block state.
     */
    Material getMaterial();

    /**
     * Get the material and metadata of this block state.
     */
    MaterialData getMaterialData();

    /**
     * Set the material of this block state. If the new material is different
     * from the current material, any metadata for the new material will be set
     * to default values.
     *
     * This changes only the state of this object, not any blocks.
     * Call {@link #update} to apply changes to actual blocks.
     */
    void setMaterial(Material material);

    /**
     * Set the material and metadata of this block state.
     *
     * This changes only the state of this object, not any blocks.
     * Call {@link #update} to apply changes to actual blocks.
     */
    void setMaterialData(MaterialData materialData);

    void reflect(BlockReflection reflection);

    void rotate(BlockRotation rotation);

    void reorient(BlockRotoflection orientation);

    void reorient(CoarseTransform transform);

    /**
     * Gets the metadata for this block
     *
     * @return block specific metadata
     * @deprecated Confusing name
     */
    @Deprecated
    MaterialData getData();

    /**
     * Gets the type of this block
     *
     * @return block type
     * @deprecated Confusing name
     */
    @Deprecated
    Material getType();

    /**
     * Gets the type-id of this block
     *
     * @return block type-id
     * @deprecated Magic value
     */
    @Deprecated
    int getTypeId();

    /**
     * Gets the light level between 0-15
     *
     * @return light level
     */
    byte getLightLevel();

    /**
     * Gets the world which contains this Block
     *
     * @return World containing this block
     */
    World getWorld();

    Vec3 getPosition();

    Vec3 tryPosition();

    /**
     * Gets the x-coordinate of this block
     *
     * @return x-coordinate
     */
    int getX();

    /**
     * Gets the y-coordinate of this block
     *
     * @return y-coordinate
     */
    int getY();

    /**
     * Gets the z-coordinate of this block
     *
     * @return z-coordinate
     */
    int getZ();

    /**
     * Gets the location of this block
     *
     * @return location
     */
    Location getLocation();

    /**
     * Stores the location of this block in the provided Location object.
     * <p>
     * If the provided Location is null this method does nothing and returns
     * null.
     *
     * @param loc the location to copy into
     * @return The Location object provided or null
     */
    Location getLocation(Location loc);

    /**
     * Gets the chunk which contains this block
     *
     * @return Containing Chunk
     */
    Chunk getChunk();

    /**
     * Sets the metadata ONLY for this block (NOT the material).
     * The {@link Material} of the given data must match that of the block.
     *
     * @param data New block specific metadata
     * @deprecated Confusing name, strange behavior
     */
    @Deprecated
    void setData(MaterialData data);

    /**
     * Sets the type of this block
     *
     * @param type Material to change this block to
     * @deprecated Confusing name
     */
    @Deprecated
    void setType(Material type);

    /**
     * Sets the type-id of this block
     *
     * @param type Type-Id to change this block to
     * @return Whether it worked?
     * @deprecated Magic value
     */
    @Deprecated
    boolean setTypeId(int type);

    /**
     * Attempts to update the block represented by this state, setting it to
     * the new values as defined by this state.
     * <p>
     * This has the same effect as calling update(false). That is to say,
     * this will not modify the state of a block if it is no longer the same
     * type as it was when this state was taken. It will return false in this
     * eventuality.
     *
     * @return true if the update was successful, otherwise false
     * @see #update(boolean)
     */
    boolean update();

    /**
     * Attempts to update the block represented by this state, setting it to
     * the new values as defined by this state.
     * <p>
     * This has the same effect as calling update(force, true). That is to
     * say, this will trigger a physics update to surrounding blocks.
     *
     * @param force true to forcefully set the state
     * @return true if the update was successful, otherwise false
     */
    boolean update(boolean force);

    /**
     * Attempts to update the block represented by this state, setting it to
     * the new values as defined by this state.
     * <p>
     * Unless force is true, this will not modify the state of a block if it
     * is no longer the same type as it was when this state was taken. It will
     * return false in this eventuality.
     * <p>
     * If force is true, it will set the type of the block to match the new
     * state, set the state data and then return true.
     * <p>
     * If applyPhysics is true, it will trigger a physics update on
     * surrounding blocks which could cause them to update or disappear.
     *
     * @param force true to forcefully set the state
     * @param applyPhysics false to cancel updating physics on surrounding
     *     blocks
     * @return true if the update was successful, otherwise false
     */
    boolean update(boolean force, boolean applyPhysics);

    /**
     * @return The data as a raw byte.
     * @deprecated Magic value
     */
    @Deprecated
    public byte getRawData();

    /**
     * @param data The new data value for the block.
     * @deprecated Magic value
     */
    @Deprecated
    public void setRawData(byte data);

    /**
     * Returns whether this state is placed in the world.
     *
     * Some methods will not work if the blockState isn't
     * placed in the world.
     *
     * @return whether the state is placed in the world
     *         or 'virtual' (e.g. on an itemstack)
     */
    boolean isPlaced();

    boolean hasPosition();

    /**
     * Spawn a {@link FallingBlock} entity with a position and material matching this block state.
     *
     * The entity is spawned in the {@link World} returned from {@link #getWorld()}.
     *
     * @throws IllegalStateException if this block state does not belong to a world
     */
    default FallingBlock spawnFallingBlock() {
        return spawnFallingBlock(getWorld());
    }

    /**
     * Spawn a {@link FallingBlock} entity in the given {@link World},
     * with a position and material matching this block state.
     */
    default FallingBlock spawnFallingBlock(World world) {
        return world.spawnFallingBlockAligned(getPosition(), getMaterialData());
    }
}
