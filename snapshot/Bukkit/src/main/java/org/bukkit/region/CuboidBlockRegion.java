package org.bukkit.region;

import java.util.Iterator;
import java.util.function.Predicate;

import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;

public class CuboidBlockRegion implements BlockRegion {

    private final Vec3 min;
    private final Vec3 max; // Upper bound is exclusive

    public static BlockRegion between(Vec3 a, Vec3 b) {
        a = a.coarseCopy();
        b = b.coarseCopy();

        if(a.coarseX() == b.coarseX() ||
           a.coarseY() == b.coarseY() ||
           a.coarseZ() == b.coarseZ()) {
            return EmptyBlockRegion.INSTANCE;
        }

        return new CuboidBlockRegion(a.minimum(b), a.maximum(b));
    }

    public static BlockRegion fromMinAndSize(Vec3 min, Vec3 size) {
        size = size.coarseCopy();
        if(size.coarseX() == 0 || size.coarseY() == 0 || size.coarseZ() == 0) {
            return EmptyBlockRegion.INSTANCE;
        }
        min = min.coarseCopy();
        return new CuboidBlockRegion(min, min.plus(size));
    }

    private CuboidBlockRegion(Vec3 min, Vec3 max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public int size() {
        final Vec3 d = max.minus(min);
        return d.coarseX() * d.coarseY() * d.coarseZ();
    }

    @Override
    public boolean contains(Vec3 position) {
        position = position.coarseCopy();
        return min.coarseLessOrEqual(position) && max.coarseGreater(position);
    }

    @Override
    public Iterator<Vec3> mutableIterator() {
        return new CuboidBlockIterator(min, max);
    }

    @Override
    public BlockRegion copy() {
        return this;
    }

    @Override
    public BlockRegion transform(CoarseTransform transform) {
        return new CuboidBlockRegion(transform.apply(min), transform.apply(max));
    }

    @Override
    public BlockRegion filter(Predicate<? super Vec3> predicate) {
        return new FilteredBlockRegion(this, predicate);
    }

    @Override
    public int hashCode() {
        final int x0 = min.coarseX(), y0 = min.coarseY(), z0 = min.coarseZ();
        final int x1 = max.coarseX(), y1 = max.coarseY(), z1 = max.coarseZ();

        // There is probably some crazy formula for this
        int hash = 0;
        for(int x = x0; x < x1; x++) {
            for(int y = y0; y < y1; y++) {
                for(int z = z0; z < z1; z++) {
                    hash += Vec3.combineHashCodes(x, y, z);
                }
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj instanceof CuboidBlockRegion) {
            return min.equals(((CuboidBlockRegion) obj).min) &&
                   max.equals(((CuboidBlockRegion) obj).max);
        }
        return standardEquals(obj);
    }
}
