package org.bukkit.util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.geometry.Vec3;

/**
 * Result of a ray-block intersection test
 */
public class RayBlockIntersection {
    private final Block block;
    private final BlockFace face;
    private final ImVector position;

    public RayBlockIntersection(Block block, BlockFace face, Vec3 position) {
        this.block = block;
        this.face = face;
        this.position = ImVector.copyOf(position);
    }

    /**
     * @return The intersected block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * @return The first intersected face of the block
     */
    public BlockFace getFace() {
        return face;
    }

    /**
     * @return The first intersected point on the surface of the block
     */
    public Vector getPosition() {
        return position;
    }
}
