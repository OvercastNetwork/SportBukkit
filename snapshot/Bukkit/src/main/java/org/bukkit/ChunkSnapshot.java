package org.bukkit;

import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Represents a static, thread-safe snapshot of chunk of blocks.
 * <p>
 * Purpose is to allow clean, efficient copy of a chunk data to be made, and
 * then handed off for processing in another thread (e.g. map rendering)
 */
public interface ChunkSnapshot {

    /**
     * Gets the X-coordinate of this chunk
     *
     * @return X-coordinate
     */
    int getX();

    /**
     * Gets the Z-coordinate of this chunk
     *
     * @return Z-coordinate
     */
    int getZ();

    /**
     * Gets name of the world containing this chunk
     *
     * @return Parent World Name
     */
    String getWorldName();

    /**
     * Get block type for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y 0-127
     * @param z 0-15
     * @return 0-255
     * @deprecated Magic value
     */
    @Deprecated
    int getBlockTypeId(int x, int y, int z);

    /**
     * Get block data for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y 0-127
     * @param z 0-15
     * @return 0-15
     * @deprecated Magic value
     */
    @Deprecated
    int getBlockData(int x, int y, int z);

    /**
     * Get the material of the block at the given position in the chunk
     */
    MaterialData getMaterialData(int x, int y, int z);

    /**
     * Get the material of the block at the given position in the chunk
     */
    MaterialData getMaterialData(Vector pos);

    /**
     * Get sky light level for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y 0-127
     * @param z 0-15
     * @return 0-15
     */
    int getBlockSkyLight(int x, int y, int z);

    /**
     * Get light level emitted by block at corresponding coordinate in the
     * chunk
     *
     * @param x 0-15
     * @param y 0-127
     * @param z 0-15
     * @return 0-15
     */
    int getBlockEmittedLight(int x, int y, int z);

    /**
     * Gets the highest non-air coordinate at the given coordinates
     *
     * @param x X-coordinate of the blocks
     * @param z Z-coordinate of the blocks
     * @return Y-coordinate of the highest non-air block
     */
    int getHighestBlockYAt(int x, int z);

    /**
     * Get biome at given coordinates
     *
     * @param x X-coordinate
     * @param z Z-coordinate
     * @return Biome at given coordinate
     */
    Biome getBiome(int x, int z);

    /**
     * Get raw biome temperature (0.0-1.0) at given coordinate
     *
     * @param x X-coordinate
     * @param z Z-coordinate
     * @return temperature at given coordinate
     */
    double getRawBiomeTemperature(int x, int z);

    /**
     * Get raw biome rainfall (0.0-1.0) at given coordinate
     *
     * @param x X-coordinate
     * @param z Z-coordinate
     * @return rainfall at given coordinate
     */
    double getRawBiomeRainfall(int x, int z);

    /**
     * Get world full time when chunk snapshot was captured
     *
     * @return time in ticks
     */
    long getCaptureFullTime();

    /**
     * Test if section is empty
     *
     * @param sy - section Y coordinate (block Y / 16)
     * @return true if empty, false if not
     */
    boolean isSectionEmpty(int sy);

    /**
     * Set the block type at the given position in the chunk.
     * Note that mutating the snapshot will compromise its thread safety.
     */
    void setBlockTypeId(int x, int y, int z, int typeId);

    /**
     * Set the block data at the given position in the chunk.
     * Note that mutating the snapshot will compromise its thread safety.
     */
    void setBlockData(int x, int y, int z, int data);

    /**
     * Set the block material (type and data) at the given position in the chunk.
     * Note that mutating the snapshot will compromise its thread safety.
     */
    void setMaterialData(int x, int y, int z, MaterialData material);

    /**
     * Set the block material (type and data) at the given position in the chunk.
     * Note that mutating the snapshot will compromise its thread safety.
     */
    void setMaterialData(Vector pos, MaterialData material);

    /**
     * Update the material (type and data) of the given block in the snapshot to
     * match the given state. The chunk coordinates saved in the snapshot are used
     * to transform the given block's position into local coordinates. If the block
     * is not inside this chunk, nothing happens.
     *
     * Note that mutating the snapshot will compromise its thread safety.
     */
    void updateBlock(BlockState state);
}
