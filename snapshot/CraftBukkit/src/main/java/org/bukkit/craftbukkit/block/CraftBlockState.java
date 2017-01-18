package org.bukkit.craftbukkit.block;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.IBlockData;
import net.minecraft.server.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.BlockReflection;
import org.bukkit.geometry.BlockRotation;
import org.bukkit.block.BlockState;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.geometry.Vec3;

public class CraftBlockState implements BlockState {
    private final @Nullable UUID worldId;
    private final BlockPosition position;
    protected int type;
    protected MaterialData data;
    protected int flag;

    public CraftBlockState(final Block block) {
        this.worldId = block.getWorld().getUID();
        this.position = BlockPosition.copyOf(block.getPosition());
        this.type = block.getTypeId();
        this.flag = 3;

        createData(block.getData());
    }

    public CraftBlockState(final Block block, int flag) {
        this(block);
        this.flag = flag;
    }

    public CraftBlockState(Material material) {
        worldId = null;
        position = null;
        type = material.getId();
    }

    public static CraftBlockState getBlockState(net.minecraft.server.World world, int x, int y, int z) {
        return new CraftBlockState(world.getWorld().getBlockAt(x, y, z));
    }

    public static CraftBlockState getBlockState(net.minecraft.server.World world, int x, int y, int z, int flag) {
        return new CraftBlockState(world.getWorld().getBlockAt(x, y, z), flag);
    }

    @Override
    public UUID getWorldId() {
        requirePlaced();
        return worldId;
    }

    @Override
    public @Nullable UUID tryWorldId() {
        return worldId;
    }

    @Override
    public CraftWorld getWorld() {
        return (CraftWorld) Bukkit.world(getWorldId());
    }

    @Override
    public BlockPosition getPosition() {
        if(position == null) {
            throw new IllegalStateException("This BlockState has no position");
        }
        return position;
    }

    @Override
    public Vec3 tryPosition() {
        return position;
    }

    public int getX() {
        return getPosition().coarseX();
    }

    public int getY() {
        return getPosition().coarseY();
    }

    public int getZ() {
        return getPosition().coarseZ();
    }

    public Chunk getChunk() {
        requirePlaced();
        return getWorld().getChunkAt(getPosition());
    }

    @Override
    public Material getMaterial() {
        return Material.getMaterial(getTypeId());
    }

    @Override
    public MaterialData getMaterialData() {
        return data;
    }

    @Override
    public void setMaterial(Material material) {
        setTypeId(material.getId());
    }

    @Override
    public void setMaterialData(MaterialData materialData) {
        this.type = materialData.getItemTypeId();
        this.data = materialData;
    }

    public void setData(final MaterialData data) {
        if(getType() != data.getItemType()) {
            throw new IllegalArgumentException("Provided data has wrong material " + data.getItemType() + ", must be " + getType());
        }
        this.data = data;
    }

    public MaterialData getData() {
        return data;
    }

    public void setType(final Material type) {
        setTypeId(type.getId());
    }

    public boolean setTypeId(final int type) {
        if (this.type != type) {
            this.type = type;

            createData((byte) 0);
        }
        return true;
    }

    public Material getType() {
        return Material.getMaterial(getTypeId());
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public int getTypeId() {
        return type;
    }

    public byte getLightLevel() {
        return getBlock().getLightLevel();
    }

    public Block getBlock() {
        return getWorld().getBlockAt(getPosition());
    }

    public boolean update() {
        return update(false);
    }

    public boolean update(boolean force) {
        return update(force, true);
    }

    public boolean update(boolean force, boolean applyPhysics) {
        requirePlaced();
        Block block = getBlock();

        if (block.getType() != getType()) {
            if (!force) {
                return false;
            }
        }

        BlockPosition pos = getPosition();
        IBlockData newBlock = CraftMagicNumbers.getBlock(getType()).fromLegacyData(getRawData());
        block.setTypeIdAndData(getTypeId(), getRawData(), applyPhysics);
        getWorld().getHandle().notify(
                pos,
                CraftMagicNumbers.getBlock(block).fromLegacyData(block.getData()),
                newBlock,
                3
        );

        // Update levers etc
        if (applyPhysics && getData() instanceof Attachable) {
            getWorld().getHandle().applyPhysics(pos.shift(CraftBlock.blockFaceToNotch(((Attachable) getData()).getAttachedFace())), newBlock.getBlock(), false);
        }

        return true;
    }

    private void createData(final byte data) {
        Material mat = getType();
        if (mat == null || mat.getData() == null) {
            this.data = new MaterialData(type, data);
        } else {
            this.data = mat.getNewData(data);
        }
    }

    public byte getRawData() {
        return data.getData();
    }

    public Location getLocation() {
        return new Location(getWorld(), getPosition());
    }

    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setPosition(getPosition());
            loc.setYaw(0);
            loc.setPitch(0);
        }

        return loc;
    }

    public void setRawData(byte data) {
        this.data.setData(data);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof BlockState)) return false;
        final BlockState that = (BlockState) obj;
        return Objects.equals(this.tryWorldId(), that.tryWorldId()) &&
               Objects.equals(this.position, that.tryPosition()) &&
               this.type == that.getTypeId() &&
               Objects.equals(this.data, that.getMaterialData());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.worldId != null ? this.worldId.hashCode() : 0);
        hash = 73 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 73 * hash + this.type;
        hash = 73 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    public TileEntity getTileEntity() {
        return null;
    }

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        requirePlaced();
        getWorld().getBlockMetadata().setMetadata(getBlock(), metadataKey, newMetadataValue);
    }

    @Override
    public MetadataValue getMetadata(String metadataKey, Plugin owningPlugin) {
        requirePlaced();
        return getWorld().getBlockMetadata().getMetadata(getBlock(), metadataKey, owningPlugin);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        requirePlaced();
        return getWorld().getBlockMetadata().getMetadata(getBlock(), metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        requirePlaced();
        return getWorld().getBlockMetadata().hasMetadata(getBlock(), metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        requirePlaced();
        getWorld().getBlockMetadata().removeMetadata(getBlock(), metadataKey, owningPlugin);
    }

    @Override
    public boolean hasPosition() {
        return position != null;
    }

    @Override
    public boolean isPlaced() {
        return worldId != null;
    }

    protected void requirePlaced() {
        if (!isPlaced()) {
            throw new IllegalStateException("The blockState must be placed to call this method");
        }
    }

    @Override
    public void reflect(BlockReflection reflection) {
        setMaterialData(Bukkit.blocks().reflect(getMaterialData(), reflection));
    }

    @Override
    public void rotate(BlockRotation rotation) {
        setMaterialData(Bukkit.blocks().rotate(getMaterialData(), rotation));
    }

    @Override
    public void reorient(BlockRotoflection orientation) {
        setMaterialData(Bukkit.blocks().transform(getMaterialData(), orientation));
    }

    @Override
    public void reorient(CoarseTransform transform) {
        setMaterialData(Bukkit.blocks().transform(getMaterialData(), transform));
    }
}