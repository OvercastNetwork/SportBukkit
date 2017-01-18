package org.bukkit.block;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Transform;
import org.bukkit.material.MaterialData;
import org.bukkit.region.BlockRegion;
import org.bukkit.geometry.Vec3;

/**
 * A set of saved block states.
 *
 * The blocks do not belong to any world. Block state, tile entity data,
 * and tile ticks can all be captured in the image.
 *
 * TODO: Provide a way to actually get {@link BlockState}s from one of these.
 */
public interface BlockImage {

    /**
     * The set of blocks included in this image. Any blocks excluded from the image
     * (e.g. air blocks) will also be exlcuded from this region.
     */
    BlockRegion region();

    MaterialData materialAt(Vec3 pos);

    int paste(World world, CoarseTransform transform);

    default int paste(World world) {
        return paste(world, Transform.identity());
    }

    default boolean pasteBlock(Vec3 pos, World world) {
        return pasteBlock(pos, world, pos);
    }

    default boolean pasteBlock(Vec3 pos, World world, CoarseTransform transform) {
        return pasteBlock(pos, world, transform.apply(pos), transform.orientation());
    }

    boolean pasteBlock(Vec3 from, World world, Vec3 to, BlockRotoflection orientation);

    default boolean pasteBlock(Vec3 from, World world, Vec3 to) {
        return pasteBlock(from, world, to, BlockRotoflection.identity());
    }

    default boolean pasteBlock(Vec3 from, Location to) {
        return pasteBlock(from, to.getWorld(), to.toVector());
    }

    default boolean pasteBlock(Vec3 from, Block to) {
        return pasteBlock(from, to.getWorld(), to.getPosition());
    }

    default BlockImage transform(CoarseTransform transform) {
        return new TransformedBlockImage(this, transform);
    }

    static BlockImage empty() {
        return EmptyBlockImage.INSTANCE;
    }
}
