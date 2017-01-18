package org.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.geometry.Vec3;
import org.bukkit.geometry.VectorFactory;
import org.bukkit.support.BukkitRuntimeTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VectorMixedResolutionArithmeticTest extends BukkitRuntimeTest {

    VectorFactory V;

    @Before
    public void setUp() throws Exception {
        V = Bukkit.vectors();
    }

    @Test
    public void addLessThanOne() throws Exception {
        final Vec3 v = V.coarse(1, 2, 3);
        assertEquals(v, v.plus(0.5));
        assertEquals(v.minus(1), v.plus(-0.5));
        assertEquals(v.minus(1), v.minus(0.5));
        assertEquals(v, v.minus(-0.5));

        assertEquals(v, v.mutableCopy().add(0.5));
        assertEquals(v.minus(1), v.mutableCopy().add(-0.5));
        assertEquals(v.minus(1), v.mutableCopy().subtract(0.5));
        assertEquals(v, v.mutableCopy().subtract(-0.5));
    }

    @Test
    public void addMoreThanOne() throws Exception {
        final Vec3 v = V.coarse(1, 2, 3);
        assertEquals(v.plus(1), v.plus(1.5));
        assertEquals(v.minus(2), v.plus(-1.5));
        assertEquals(v.minus(2), v.minus(1.5));
        assertEquals(v.plus(1), v.minus(-1.5));

        assertEquals(v.plus(1), v.mutableCopy().add(1.5));
        assertEquals(v.minus(2), v.mutableCopy().add(-1.5));
        assertEquals(v.minus(2), v.mutableCopy().subtract(1.5));
        assertEquals(v.plus(1), v.mutableCopy().subtract(-1.5));
    }

    @Test
    public void multiply() throws Exception {
        final Vec3 v = V.coarse(100, 200, 300);
        assertEquals(V.coarse(50, 100, 150), v.times(0.5));
        assertEquals(V.coarse(-50, -100, -150), v.times(-0.5));
    }
}
