package org.bukkit.block;

import org.bukkit.World;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;
import org.bukkit.material.MaterialData;
import org.bukkit.region.BlockRegion;

public class TransformedBlockImage implements BlockImage {

    private final BlockImage original;
    private final CoarseTransform transform;
    private final CoarseTransform inverse;

    public TransformedBlockImage(BlockImage original, CoarseTransform transform) {
        this.original = original;
        this.transform = transform;
        this.inverse = transform.inverse();
    }

    @Override
    public BlockRegion region() {
        return original.region().transform(transform);
    }

    @Override
    public MaterialData materialAt(Vec3 pos) {
        return original.materialAt(inverse.apply(pos));
    }

    @Override
    public int paste(World world, CoarseTransform transform) {
        return original.paste(world, this.transform.andThen(transform));
    }

    @Override
    public boolean pasteBlock(Vec3 from, World world, Vec3 to, BlockRotoflection orientation) {
        return pasteBlock(from, world, CoarseTransform.translation(to).andThen(orientation.transform()));
    }

    @Override
    public BlockImage transform(CoarseTransform transform) {
        if(transform.isIdentity()) return this;
        transform = this.transform.andThen(transform);
        if(transform.isIdentity()) return original;
        return new TransformedBlockImage(original, transform);
    }
}
