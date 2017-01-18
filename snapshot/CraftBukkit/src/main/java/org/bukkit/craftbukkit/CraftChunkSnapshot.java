package org.bukkit.craftbukkit;

import java.util.Arrays;

import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;

import net.minecraft.server.BiomeBase;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Represents a static, thread-safe snapshot of chunk of blocks
 * Purpose is to allow clean, efficient copy of a chunk data to be made, and then handed off for processing in another thread (e.g. map rendering)
 */
public class CraftChunkSnapshot implements ChunkSnapshot {
    private final int x, z;
    private final String worldname;
    private final short[][] blockids; /* Block IDs, by section */
    private final byte[][] blockdata;
    private final byte[][] skylight;
    private final byte[][] emitlight;
    private final boolean[] empty;
    private final int[] hmap; // Height map
    private final long captureFulltime;
    private final BiomeBase[] biome;
    private final double[] biomeTemp;
    private final double[] biomeRain;

    CraftChunkSnapshot(int x, int z, String wname, long wtime, short[][] sectionBlockIDs, byte[][] sectionBlockData, byte[][] sectionSkyLights, byte[][] sectionEmitLights, boolean[] sectionEmpty, int[] hmap, BiomeBase[] biome, double[] biomeTemp, double[] biomeRain) {
        this.x = x;
        this.z = z;
        this.worldname = wname;
        this.captureFulltime = wtime;
        this.blockids = sectionBlockIDs;
        this.blockdata = sectionBlockData;
        this.skylight = sectionSkyLights;
        this.emitlight = sectionEmitLights;
        this.empty = sectionEmpty;
        this.hmap = hmap;
        this.biome = biome;
        this.biomeTemp = biomeTemp;
        this.biomeRain = biomeRain;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public String getWorldName() {
        return worldname;
    }

    public final int getBlockTypeId(int x, int y, int z) {
        return blockids[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
    }

    public final int getBlockData(int x, int y, int z) {
        int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        return (blockdata[y >> 4][off] >> ((x & 1) << 2)) & 0xF;
    }

    @Override
    public MaterialData getMaterialData(int x, int y, int z) {
        return new MaterialData(getBlockTypeId(x, y, z), (byte) getBlockData(x, y, z));
    }

    @Override
    public MaterialData getMaterialData(Vector pos) {
        return getMaterialData(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    public final int getBlockSkyLight(int x, int y, int z) {
        int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        return (skylight[y >> 4][off] >> ((x & 1) << 2)) & 0xF;
    }

    public final int getBlockEmittedLight(int x, int y, int z) {
        int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        return (emitlight[y >> 4][off] >> ((x & 1) << 2)) & 0xF;
    }

    public final int getHighestBlockYAt(int x, int z) {
        return hmap[z << 4 | x];
    }

    public final Biome getBiome(int x, int z) {
        return CraftBlock.biomeBaseToBiome(biome[z << 4 | x]);
    }

    public final double getRawBiomeTemperature(int x, int z) {
        return biomeTemp[z << 4 | x];
    }

    public final double getRawBiomeRainfall(int x, int z) {
        return biomeRain[z << 4 | x];
    }

    public final long getCaptureFullTime() {
        return captureFulltime;
    }

    public final boolean isSectionEmpty(int sy) {
        return empty[sy];
    }

    private void ensureSectionUnshared(int sy) {
        if(empty[sy]) {
            empty[sy] = false;
            blockids[sy] = new short[4096];
            blockdata[sy] = new byte[2048];
            emitlight[sy] = new byte[2048];
            skylight[sy] = new byte[2048];
            Arrays.fill(skylight[sy], (byte) 0xff);
        }
    }

    public void setBlockTypeId(int x, int y, int z, int typeId) {
        int sy = y >> 4;
        ensureSectionUnshared(sy);
        blockids[sy][((y & 0xF) << 8) | (z << 4) | x] = (short) typeId;
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        int sy = y >> 4;
        ensureSectionUnshared(sy);
        int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        data &= 0xf;
        int packed = blockdata[sy][off];
        if((x & 1) == 0) {
            packed = (packed & 0xf0) | data;
        } else {
            packed = (packed & 0x0f) | (data << 4);
        }
        blockdata[sy][off] = (byte) packed;
    }

    @Override
    public void setMaterialData(int x, int y, int z, MaterialData material) {
        setBlockTypeId(x, y, z, material.getItemTypeId());
        setBlockData(x, y, z, material.getData());
    }

    @Override
    public void setMaterialData(Vector pos, MaterialData material) {
        setMaterialData(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), material);
    }

    @Override
    public void updateBlock(BlockState state) {
        // Can't update light level because BlockState doesn't distinguish between skylight and block light
        int x = state.getX() - (getX() << 4);
        int z = state.getZ() - (getZ() << 4);
        if(x >= 0 && x < 16 && z >= 0 && z < 16 && state.getY() >= 0 && state.getY() < 256) {
            setBlockTypeId(x, state.getY(), z, state.getTypeId());
            setBlockData(x, state.getY(), z, state.getRawData());
        }
    }
}
