package org.bukkit.block;

import org.bukkit.support.BukkitRuntimeTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockFaceTest extends BukkitRuntimeTest {

    @Test
    public void lookupByYaw() throws Exception {
        assertEquals(BlockFace.SOUTH, BlockFace.byBlockYaw(0));
        assertEquals(BlockFace.WEST, BlockFace.byBlockYaw(4));
        assertEquals(BlockFace.NORTH, BlockFace.byBlockYaw(8));
        assertEquals(BlockFace.EAST, BlockFace.byBlockYaw(12));

        for(int i = 0; i < 16; i++) {
            final BlockFace face = BlockFace.byBlockYaw(i);
            assertEquals(i, face.blockYaw());
        }
    }

    @Test
    public void lookupByDirection() throws Exception {
        // vertical
        assertEquals(BlockFace.SELF, BlockFace.byDirection(0, 0, 0));
        assertEquals(BlockFace.DOWN, BlockFace.byDirection(0, -1, 0));
        assertEquals(BlockFace.UP, BlockFace.byDirection(0, 1, 0));

        // cardinals
        assertEquals(BlockFace.WEST, BlockFace.byDirection(-1, 0, 0));
        assertEquals(BlockFace.EAST, BlockFace.byDirection(1, 0, 0));
        assertEquals(BlockFace.NORTH, BlockFace.byDirection(0, 0, -1));
        assertEquals(BlockFace.SOUTH, BlockFace.byDirection(0, 0, 1));

        // diagonals
        assertEquals(BlockFace.NORTH_WEST, BlockFace.byDirection(-1, 0, -1));
        assertEquals(BlockFace.NORTH_EAST, BlockFace.byDirection(1, 0, -1));
        assertEquals(BlockFace.SOUTH_WEST, BlockFace.byDirection(-1, 0, 1));
        assertEquals(BlockFace.SOUTH_EAST, BlockFace.byDirection(1, 0, 1));

        // Z major
        assertEquals(BlockFace.NORTH_NORTH_WEST, BlockFace.byDirection(-1, 0, -2));
        assertEquals(BlockFace.NORTH_NORTH_EAST, BlockFace.byDirection(1, 0, -2));
        assertEquals(BlockFace.SOUTH_SOUTH_WEST, BlockFace.byDirection(-1, 0, 2));
        assertEquals(BlockFace.SOUTH_SOUTH_EAST, BlockFace.byDirection(1, 0, 2));

        // X major
        assertEquals(BlockFace.WEST_NORTH_WEST, BlockFace.byDirection(-2, 0, -1));
        assertEquals(BlockFace.EAST_NORTH_EAST, BlockFace.byDirection(2, 0, -1));
        assertEquals(BlockFace.WEST_SOUTH_WEST, BlockFace.byDirection(-2, 0, 1));
        assertEquals(BlockFace.EAST_SOUTH_EAST, BlockFace.byDirection(2, 0, 1));

        for(BlockFace face : BlockFace.values()) {
            assertTrue(face.coarseEquals(face.normal()));
            assertEquals(face, BlockFace.byDirection(face.normal()));
        }
    }
}
