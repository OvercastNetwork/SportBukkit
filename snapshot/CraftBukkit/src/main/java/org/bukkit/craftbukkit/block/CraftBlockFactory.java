package org.bukkit.craftbukkit.block;

import net.minecraft.server.EnumBlockMirror;
import net.minecraft.server.EnumBlockRotation;
import net.minecraft.server.IBlockData;
import org.bukkit.block.BlockFactory;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.BlockReflection;
import org.bukkit.geometry.BlockRotation;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.material.MaterialData;

public class CraftBlockFactory implements BlockFactory {

    private static final CraftBlockFactory INSTANCE = new CraftBlockFactory();
    public static CraftBlockFactory instance() { return INSTANCE; }

    public static BlockReflection toBukkit(EnumBlockMirror nms) {
        switch(nms) {
            case NONE: return BlockReflection.NONE;
            case LEFT_RIGHT: return BlockReflection.LEFT_RIGHT;
            case FRONT_BACK: return BlockReflection.FRONT_BACK;
        }
        throw new IllegalStateException();
    }

    public static EnumBlockMirror toNms(BlockReflection bukkit) {
        switch(bukkit) {
            case NONE: return EnumBlockMirror.NONE;
            case LEFT_RIGHT: return EnumBlockMirror.LEFT_RIGHT;
            case FRONT_BACK: return EnumBlockMirror.FRONT_BACK;
        }
        throw new IllegalStateException();
    }

    public static BlockRotation toBukkit(EnumBlockRotation nms) {
        switch(nms) {
            case NONE: return BlockRotation.NONE;
            case CLOCKWISE_90: return BlockRotation.CLOCKWISE_90;
            case CLOCKWISE_180: return BlockRotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90: return BlockRotation.COUNTERCLOCKWISE_90;
        }
        throw new IllegalStateException();
    }

    public static EnumBlockRotation toNms(BlockRotation nms) {
        switch(nms) {
            case NONE: return EnumBlockRotation.NONE;
            case CLOCKWISE_90: return EnumBlockRotation.CLOCKWISE_90;
            case CLOCKWISE_180: return EnumBlockRotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90: return EnumBlockRotation.COUNTERCLOCKWISE_90;
        }
        throw new IllegalStateException();
    }

    public IBlockData transform(IBlockData data, BlockRotoflection transform) {
        if(transform.isIdentity()) return data;

        // Mojang applies reflection first, so we do too
        data = data.a(toNms(transform.reflection()));
        data = data.a(toNms(transform.rotation()));
        return data;
    }

    @Override
    public MaterialData transform(MaterialData material, BlockRotoflection transform) {
        if(transform.isIdentity()) return material;

        return CraftMagicNumbers.nmsBlockStateToMaterialData(
            transform(CraftMagicNumbers.getBlockData(material), transform)
        );
    }

    @Override
    public MaterialData transform(MaterialData material, CoarseTransform transform) {
        return transform(material, transform.orientation());
    }

    @Override
    public MaterialData reflect(MaterialData material, BlockReflection reflection) {
        if(reflection == BlockReflection.NONE) return material;
        return transform(material, BlockRotoflection.of(reflection));
    }

    @Override
    public MaterialData rotate(MaterialData material, BlockRotation rotation) {
        if(rotation == BlockRotation.NONE) return material;
        return transform(material, BlockRotoflection.of(rotation));
    }
}
