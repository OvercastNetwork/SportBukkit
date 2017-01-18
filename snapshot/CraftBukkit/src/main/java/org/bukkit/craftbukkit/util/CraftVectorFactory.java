package org.bukkit.craftbukkit.util;

import net.minecraft.server.BlockPosition;
import org.bukkit.util.ImVector;
import org.bukkit.geometry.MutableVec3;
import org.bukkit.util.NumberConversions;
import org.bukkit.geometry.Vec3;
import org.bukkit.util.Vector;
import org.bukkit.geometry.VectorFactory;

public class CraftVectorFactory implements VectorFactory {

    @Override
    public Vec3 coarse(int x, int y, int z) {
        return BlockPosition.of(x, y, z);
    }

    @Override
    public Vec3 coarse(double x, double y, double z) {
        return BlockPosition.of(NumberConversions.floor(x),
                                NumberConversions.floor(y),
                                NumberConversions.floor(z));
    }

    @Override
    public Vec3 fine(int x, int y, int z) {
        return ImVector.of(x, y, z);
    }

    @Override
    public Vec3 fine(double x, double y, double z) {
        return ImVector.of(x, y, z);
    }

    @Override
    public Vec3 coarseZero() {
        return BlockPosition.ZERO;
    }

    @Override
    public MutableVec3 coarseMutable(int x, int y, int z) {
        return new BlockPosition.MutableBlockPosition(x, y, z);
    }

    @Override
    public MutableVec3 fineMutable(double x, double y, double z) {
        return new Vector(x, y, z);
    }
}
