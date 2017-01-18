package org.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.geometry.Vec3;
import org.bukkit.geometry.VectorFactory;
import org.bukkit.support.BukkitRuntimeTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VectorFactoryTest extends BukkitRuntimeTest {

    VectorFactory V;

    @Before
    public final void initVectorFactory() throws Exception {
        V = Bukkit.vectors();
    }

    void assertCoarse(int x, int y, int z, Vec3 v) {
        assertTrue(v + " should be coarse", v.isCoarse());
        assertFalse(v + " should not be fine", v.isFine());

        assertEquals(x, v.coarseX());
        assertEquals(y, v.coarseY());
        assertEquals(z, v.coarseZ());
    }

    void assertFine(double x, double y, double z, Vec3 v) {
        assertTrue(v + " should be fine", v.isFine());
        assertFalse(v + " should not be coarse", v.isCoarse());

        assertEquals(x, v.fineX(), 0D);
        assertEquals(y, v.fineY(), 0D);
        assertEquals(z, v.fineZ(), 0D);
    }

    @Test
    public void immutable() throws Exception {
        assertCoarse(1, 2, 3, V.coarse(1, 2, 3));
        assertCoarse(0, 0, 0, V.coarseZero());
        assertFine(1.5, 2.5, 3.5, V.fine(1.5, 2.5, 3.5));
        assertFine(0, 0, 0, V.fineZero());
    }

    @Test
    public void mutable() throws Exception {
        Vec3 v;

        v = V.coarseMutable(1, 2, 3);
        assertCoarse(1, 2, 3, v);
        assertTrue(v.isMutable());

        v = V.fineMutable(1.5, 2.5, 3.5);
        assertFine(1.5, 2.5, 3.5, v);
        assertTrue(v.isMutable());
    }
}
