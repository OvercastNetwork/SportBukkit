package org.bukkit.block;

import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.BlockReflection;
import org.bukkit.geometry.BlockRotation;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.material.MaterialData;

/**
 * Construct and transform block {@link MaterialData}
 */
public interface BlockFactory {

    MaterialData reflect(MaterialData material, BlockReflection reflection);

    MaterialData rotate(MaterialData material, BlockRotation rotation);

    MaterialData transform(MaterialData material, BlockRotoflection transform);

    MaterialData transform(MaterialData material, CoarseTransform transform);
}
