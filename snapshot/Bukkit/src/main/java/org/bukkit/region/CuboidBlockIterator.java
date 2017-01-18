package org.bukkit.region;

import java.util.NoSuchElementException;

import org.bukkit.geometry.MutableVec3;
import org.bukkit.geometry.Vec3;

public class CuboidBlockIterator extends MutableBlockIterator {

    private final int xMin, yMin;
    private final int xMax, yMax, zMax;

    private int x, y, z;
    private boolean hasNext;

    public CuboidBlockIterator(Vec3 min, Vec3 max) {
        x = this.xMin = min.coarseX();
        y = this.yMin = min.coarseY();
        z = min.coarseZ();

        this.xMax = max.coarseX();
        this.yMax = max.coarseY();
        this.zMax = max.coarseZ();

        this.hasNext = x < xMax && y < yMax && z < zMax;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    protected void advance(MutableVec3 value) {
        if(!hasNext) {
            throw new NoSuchElementException();
        }

        value.set(x, y, z);

        if(++x >= xMax) {
            x = xMin;
            if(++y >= yMax) {
                y = yMin;
                if(++z >= zMax) {
                    hasNext = false;
                }
            }
        }
    }
}
