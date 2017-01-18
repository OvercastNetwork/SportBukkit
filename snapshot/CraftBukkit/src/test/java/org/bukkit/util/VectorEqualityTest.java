package org.bukkit.util;

import net.minecraft.server.BlockPosition;
import org.junit.Test;

import static org.junit.Assert.*;
import static tc.oc.test.Assert.*;

public class VectorEqualityTest {

    @Test
    public void coarseEquality() throws Exception {
        assertMutuallyEqual(new BlockPosition(1, 2, 3), new BlockVector(1, 2, 3));
        assertMutuallyEqual(new BlockPosition(1, 2, 3), new BlockVector(1.5, 2.5, 3.5));
    }

    @Test
    public void coarseInequality() throws Exception {
        // Coarse vectors with different values
        assertMutuallyUnequal(new BlockPosition(1, 2, 3), new BlockVector(4, 5, 6));

        // Mix coarse and fine vectors
        assertMutuallyUnequal(new BlockPosition(1, 2, 3), new Vector(1, 2, 3));
        assertMutuallyUnequal(new BlockVector(1, 2, 3), new Vector(1, 2, 3));
    }
}
