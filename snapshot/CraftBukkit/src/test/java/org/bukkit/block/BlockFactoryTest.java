package org.bukkit.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.BlockReflection;
import org.bukkit.geometry.BlockRotation;
import org.bukkit.material.Directional;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.support.BukkitRuntimeTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockFactoryTest extends BukkitRuntimeTest {

    void normalize(MaterialData data) {
        final Directional directional = (Directional) data;
        directional.setFacingDirection(directional.getFacing());
    }

    void test(MaterialData material, BlockRotoflection rotoflection) {
        normalize(material);

        // Transform with the BlockOrienter
        final MaterialData actual = Bukkit.blocks().transform(material, rotoflection);

        // Transform through the Directional interface
        final MaterialData expected = material.clone();
        if(expected instanceof Lever) {
            final Lever lever = (Lever) expected;
            lever.setFacingDirection(rotoflection.transform().apply(lever.getFacing()),
                                     rotoflection.apply(lever.getAxis()));
        } else {
            final Directional directional = (Directional) expected;
            final BlockFace facing = directional.getFacing();
            directional.setFacingDirection(rotoflection.transform().apply(facing));
        }

        // Assert identical results
        assertEquals("material=" + material.getItemType() + " data=" + material + " rotoflection=" + rotoflection,
                     expected, actual);
    }

    @Test
    public void reorientDirectionalBlocks() throws Exception {
        for(Material type : Material.values()) {
            if(Directional.class.isAssignableFrom(type.getData())) {
                final MaterialData material = type.getNewData((byte) 0);
                for(BlockRotation rotation : BlockRotation.values()) {
                    test(material, BlockRotoflection.of(BlockReflection.NONE, rotation));
                    test(material, BlockRotoflection.of(BlockReflection.X, rotation));
                }
            }
        }
    }
}
