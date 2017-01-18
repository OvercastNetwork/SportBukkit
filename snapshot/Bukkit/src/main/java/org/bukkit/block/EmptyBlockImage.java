package org.bukkit.block;

import org.bukkit.World;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;
import org.bukkit.material.MaterialData;
import org.bukkit.region.BlockRegion;

public class EmptyBlockImage implements BlockImage {

    static final EmptyBlockImage INSTANCE = new EmptyBlockImage();

    private EmptyBlockImage() {}

    @Override
    public BlockRegion region() {
        return BlockRegion.empty();
    }

    @Override
    public MaterialData materialAt(Vec3 pos) {
        return null;
    }

    @Override
    public int paste(World world, CoarseTransform transform) {
        return 0;
    }

    @Override
    public boolean pasteBlock(Vec3 from, World world, Vec3 to, BlockRotoflection orientation) {
        return false;
    }

    @Override
    public BlockImage transform(CoarseTransform transform) {
        return this;
    }
}
