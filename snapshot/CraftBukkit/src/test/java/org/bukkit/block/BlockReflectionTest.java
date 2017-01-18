package org.bukkit.block;

import org.bukkit.geometry.BlockReflection;
import org.bukkit.support.BukkitRuntimeTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockReflectionTest extends BukkitRuntimeTest {

    static String swap(String in, String a, String b) {
        return in.replace(a, "*")
                 .replace(b, a)
                 .replace("*", b);
    }

    void applyToFace(BlockReflection reflection, String here, String there) {
        for(BlockFace face : BlockFace.values()) {
            BlockFace expected = BlockFace.valueOf(swap(face.name(), here, there));
            BlockFace actual = reflection.transform().apply(face);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void applyToFace() throws Exception {
        applyToFace(BlockReflection.NORTH_SOUTH, "NORTH", "SOUTH");
        applyToFace(BlockReflection.EAST_WEST, "EAST", "WEST");
    }
}
