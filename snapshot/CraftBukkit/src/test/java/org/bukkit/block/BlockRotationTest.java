package org.bukkit.block;

import org.bukkit.Bukkit;
import org.bukkit.geometry.Axis;
import org.bukkit.geometry.BlockRotation;
import org.bukkit.geometry.VectorFactory;
import org.bukkit.support.BukkitRuntimeTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockRotationTest extends BukkitRuntimeTest {
    @Test
    public void applyToVector() throws Exception {
        VectorFactory V = Bukkit.vectors();
        assertEquals(V.coarse( 0, 0,  1), BlockRotation.turns(1).transform().apply(V.coarse(1, 0, 0)));
        assertEquals(V.coarse(-1, 0,  0), BlockRotation.turns(1).transform().apply(V.coarse(0, 0, 1)));
        assertEquals(V.coarse( 0, 0, -1), BlockRotation.turns(1).transform().apply(V.coarse(-1, 0, 0)));
        assertEquals(V.coarse( 1, 0,  0), BlockRotation.turns(1).transform().apply(V.coarse(0, 0, -1)));
    }

    @Test
    public void applyToFace() throws Exception {
        for(int turns = 1; turns < 4; turns++) {
            for(BlockFace face : BlockFace.values()) {
                BlockFace expected = face.isHorizontal() ? BlockFace.byBlockYaw(face.blockYaw() + turns * 4)
                                                         : face;
                BlockFace actual = BlockRotation.turns(turns).transform().apply(face);
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    public void applytoAxis() throws Exception {
        assertEquals(Axis.X, BlockRotation.NONE.apply(Axis.X));
        assertEquals(Axis.Y, BlockRotation.NONE.apply(Axis.Y));
        assertEquals(Axis.Z, BlockRotation.NONE.apply(Axis.Z));

        assertEquals(Axis.Z, BlockRotation.CLOCKWISE_90.apply(Axis.X));
        assertEquals(Axis.Y, BlockRotation.CLOCKWISE_90.apply(Axis.Y));
        assertEquals(Axis.X, BlockRotation.CLOCKWISE_90.apply(Axis.Z));

        assertEquals(Axis.X, BlockRotation.CLOCKWISE_180.apply(Axis.X));
        assertEquals(Axis.Y, BlockRotation.CLOCKWISE_180.apply(Axis.Y));
        assertEquals(Axis.Z, BlockRotation.CLOCKWISE_180.apply(Axis.Z));

        assertEquals(Axis.Z, BlockRotation.COUNTERCLOCKWISE_90.apply(Axis.X));
        assertEquals(Axis.Y, BlockRotation.COUNTERCLOCKWISE_90.apply(Axis.Y));
        assertEquals(Axis.X, BlockRotation.COUNTERCLOCKWISE_90.apply(Axis.Z));
    }
}
