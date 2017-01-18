package org.bukkit.craftbukkit.block;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Blocks;
import net.minecraft.server.IBlockData;
import net.minecraft.server.IInventory;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NextTickListEntry;
import net.minecraft.server.StructureBoundingBox;
import net.minecraft.server.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockImage;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.material.MaterialData;
import org.bukkit.region.BlockRegion;
import org.bukkit.geometry.Vec3;

/**
 * Read/write algorithms are derived from the code for the /clone command,
 * which can be found in {@link net.minecraft.server.CommandClone}.
 */
public class CraftBlockImage implements BlockImage {

    static class BlockRecord extends BlockPosition {
        final IBlockData blockData;
        final NBTTagCompound tileEntityData;
        NextTickListEntry tickListEntry;

        BlockRecord(Vec3 pos, IBlockData blockData, NBTTagCompound tileEntityData) {
            super(pos.coarseX(), pos.coarseY(), pos.coarseZ());
            this.blockData = blockData;
            this.tileEntityData = tileEntityData;
        }

        void pasteTileEntity(net.minecraft.server.World world, BlockPosition pos) {
            final TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntityData != null && tileEntity != null) {
                tileEntityData.setInt("x", pos.getX());
                tileEntityData.setInt("y", pos.getY());
                tileEntityData.setInt("z", pos.getZ());

                tileEntity.a(tileEntityData);
                tileEntity.update();
            }
        }

        void pasteTick(net.minecraft.server.World world, BlockPosition pos, long tickTime) {
            if(tickListEntry != null) {
                world.b(pos, tickListEntry.a(), (int) (tickListEntry.b - tickTime), tickListEntry.c);
            }
        }
    }

    private final ImmutableMap<Vec3, BlockRecord> byPosition;
    private final ImmutableList<BlockRecord> earlyBlocks;
    private final ImmutableList<BlockRecord> tileEntities;
    private final ImmutableList<BlockRecord> lateBlocks;
    private final ImmutableList<BlockRecord> tickedBlocks;
    private final long tickTime;

    @Override
    public BlockRegion region() {
        return BlockRegion.of(byPosition.keySet());
    }

    @Override
    public MaterialData materialAt(Vec3 pos) {
        final BlockRecord r = byPosition.get(pos.coarseCopy());
        return r == null ? null : CraftMagicNumbers.nmsBlockStateToMaterialData(r.blockData);
    }

    @Override
    public boolean pasteBlock(Vec3 from, World world, Vec3 to, BlockRotoflection orientation) {
        final BlockRecord record = byPosition.get(from.coarseCopy());
        if(record == null) return false;

        final CraftBlockFactory orienter = (CraftBlockFactory) Bukkit.blocks();
        final net.minecraft.server.World nmsWorld = ((CraftWorld) world).getHandle();
        final BlockPosition pos = BlockPosition.copyOf(to);
        record.pasteTileEntity(nmsWorld, pos);
        nmsWorld.setTypeAndData(pos, orienter.transform(record.blockData, orientation), 2);
        record.pasteTick(nmsWorld, pos, tickTime);
        return true;
    }

    public CraftBlockImage(CraftWorld craftWorld, BlockRegion region, boolean includeAir, boolean clearSource) {
        final net.minecraft.server.WorldServer world = craftWorld.getHandle();
        final BlockPosition.MutableBlockPosition mutablePosition = new BlockPosition.MutableBlockPosition();

        final ImmutableMap.Builder<Vec3, BlockRecord> byPosition = ImmutableMap.builder();
        final ImmutableList.Builder<BlockRecord> earlyBlocks = ImmutableList.builder();
        final ImmutableList.Builder<BlockRecord> tileEntities = ImmutableList.builder();
        final ImmutableList.Builder<BlockRecord> lateBlocks = ImmutableList.builder();

        for(Vec3 position : region.mutableIterable()) {
            // Avoid creating any objects for air blocks, as long as the region
            // iterator is spitting out NMS BlockPositions.
            mutablePosition.set(position);
            final IBlockData blockData = world.getType(mutablePosition);

            if(includeAir || blockData.getBlock() != Blocks.AIR) {
                final BlockRecord block;

                TileEntity tileEntity = world.getTileEntity(mutablePosition);
                if(tileEntity != null) {
                    block = new BlockRecord(mutablePosition, blockData, new NBTTagCompound());
                    tileEntity.save(block.tileEntityData);
                    tileEntities.add(block);

                    if(clearSource && tileEntity instanceof IInventory) {
                        ((IInventory) tileEntity).clear(); // Clear inventory
                    }
                } else {
                    block = new BlockRecord(mutablePosition, blockData, null);
                    if (!block.blockData.b() && // flammable
                        !block.blockData.h()) { // full-sized
                        lateBlocks.add(block);
                    } else {
                        earlyBlocks.add(block);
                    }
                }

                byPosition.put(block, block);
            }
        }

        this.byPosition = byPosition.build();
        this.earlyBlocks = earlyBlocks.build();
        this.lateBlocks = lateBlocks.build();
        this.tileEntities = tileEntities.build();

        if(clearSource) {
            final Iterable<BlockRecord> clearPositions = Iterables.concat(this.lateBlocks, this.tileEntities, this.earlyBlocks);

            for(BlockRecord block : clearPositions) {
                if(block.tileEntityData != null) {
                    final TileEntity tileEntity = world.getTileEntity(block);
                    if(tileEntity instanceof IInventory) {
                        ((IInventory) tileEntity).clear();
                    }
                }
                world.setTypeAndData(block, Blocks.BARRIER.getBlockData(), 2);
            }

            for(BlockRecord block : clearPositions) {
                world.setTypeAndData(block, Blocks.AIR.getBlockData(), 3);
            }
        }

        this.tickTime = world.getWorldData().getTime();

        final ImmutableList.Builder<BlockRecord> tickedBlocks = ImmutableList.builder();
        final List<NextTickListEntry> ticks = world.a(new StructureBoundingBox(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE), false);
        if(ticks != null) {
            for(NextTickListEntry tick : ticks) {
                final BlockRecord block = this.byPosition.get(tick.a);
                if(block != null) {
                    block.tickListEntry = tick;
                    tickedBlocks.add(block);
                }
            }
        }
        this.tickedBlocks = tickedBlocks.build();
    }

    @Override
    public int paste(World world, CoarseTransform transform) {
        final net.minecraft.server.WorldServer nmsWorld = ((CraftWorld) world).getHandle();

        final Iterable<BlockRecord> allBlocks = Iterables.concat(earlyBlocks, tileEntities, lateBlocks);
        final Iterable<BlockRecord> reversedBlocks = Iterables.concat(lateBlocks, tileEntities, earlyBlocks);

        final BlockPosition.MutableBlockPosition mutablePosition = new BlockPosition.MutableBlockPosition();

        // Fill the entire destination region with barrier blocks
        for(BlockRecord block : reversedBlocks) {
            mutablePosition.set(block);
            transform.applyInPlace(mutablePosition);

            // Clear any containers so they don't spill their contents
            final TileEntity tileEntity = nmsWorld.getTileEntity(mutablePosition);
            if (tileEntity instanceof IInventory) {
                ((IInventory) tileEntity).clear(); // Clear inventory
            }

            nmsWorld.setTypeAndData(mutablePosition, Blocks.BARRIER.getBlockData(), 2);
        }

        int affectedBlocks = 0;
        final CraftBlockFactory orienter = (CraftBlockFactory) Bukkit.blocks();
        final BlockRotoflection orientation = transform.orientation();

        for(BlockRecord block : allBlocks) {
            mutablePosition.set(block);
            transform.applyInPlace(mutablePosition);
            if(nmsWorld.setTypeAndData(mutablePosition, orienter.transform(block.blockData, orientation), 2)) {
                ++affectedBlocks;
            }
        }

        for(BlockRecord block : tileEntities) {
            mutablePosition.set(block);
            transform.applyInPlace(mutablePosition);
            block.pasteTileEntity(nmsWorld, mutablePosition);
            nmsWorld.setTypeAndData(mutablePosition, orienter.transform(block.blockData, orientation), 2);
        }

        for(BlockRecord block : reversedBlocks) {
            mutablePosition.set(block);
            transform.applyInPlace(mutablePosition);
            nmsWorld.update(mutablePosition, block.blockData.getBlock(), false);
        }

        for(BlockRecord block : tickedBlocks) {
            mutablePosition.set(block.tickListEntry.a);
            transform.applyInPlace(mutablePosition);
            block.pasteTick(nmsWorld, mutablePosition, tickTime);
        }

        return affectedBlocks;
    }
}
