package org.bukkit.util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.geometry.Vec3;

/**
 * An immutable {@link Vector}
 *
 * All the mutating methods inherited from {@link Vector}
 * will throw an exception.
 */
public class ImVector extends Vector {

    private static final ImVector ZERO = new ImVector(0, 0, 0);
    private static final ImVector NaN = new ImVector(Double.NaN, Double.NaN, Double.NaN);

    ImVector(double x, double y, double z) {
        super(x, y, z);
    }

    public static ImVector of(double x, double y, double z) {
        return x == 0 && y == 0 && z == 0 ? ZERO : new ImVector(x, y, z);
    }

    public static ImVector of(double n) {
        return n == 0 ? ZERO : new ImVector(n, n, n);
    }

    public static ImVector ofZero() {
        return ZERO;
    }

    public static ImVector ofNaN() {
        return NaN;
    }

    public static ImVector copyOf(Vec3 v) {
        return v instanceof ImVector ? (ImVector) v
                                     : new ImVector(v.fineX(), v.fineY(), v.fineZ());
    }

    private static class Corner extends ImVector {
        public Corner(int x, int y, int z) {
            super((double) x, (double) y, (double) z);
        }

        @Override public boolean isBlockCorner() { return true; }
        @Override public boolean isBlockCenter() { return false; }
    }

    private static class Center extends ImVector {
        Center(int x, int y, int z) {
            super(x + 0.5, y + 0.5, z + 0.5);
        }

        @Override public boolean isBlockCorner() { return false; }
        @Override public boolean isBlockCenter() { return true; }
    }

    public static ImVector cornerOf(int x, int y, int z) {
        return new Corner(x, y, z);
    }

    public static ImVector cornerOf(Vec3 v) {
        return v instanceof Corner ? (Corner) v : new Corner(v.coarseX(), v.coarseY(), v.coarseZ());
    }

    public static ImVector cornerOf(Block block) {
        return new Corner(block.getX(), block.getY(), block.getZ());
    }

    public static ImVector cornerOf(BlockState block) {
        return new Corner(block.getX(), block.getY(), block.getZ());
    }

    public static ImVector centerOf(int x, int y, int z) {
        return new Center(x, y, z);
    }

    public static ImVector centerOf(Vec3 v) {
        return v instanceof Center ? (Center) v : new Center(v.coarseX(), v.coarseY(), v.coarseZ());
    }

    public static ImVector centerOf(Block block) {
        return new Center(block.getX(), block.getY(), block.getZ());
    }

    public static ImVector centerOf(BlockState block) {
        return new Center(block.getX(), block.getY(), block.getZ());
    }

    public static ImVector minimum(Vec3 a, Vec3 b) {
        return of(Math.min(a.fineX(), b.fineX()),
                  Math.min(a.fineY(), b.fineY()),
                  Math.min(a.fineZ(), b.fineZ()));
    }

    public static ImVector maximum(Vec3 a, Vec3 b) {
        return of(Math.max(a.fineX(), b.fineX()),
                  Math.max(a.fineY(), b.fineY()),
                  Math.max(a.fineZ(), b.fineZ()));
    }

    public static ImVector min(Vec3... a) {
        double x, y, z;
        x = y = z = Double.POSITIVE_INFINITY;
        for(Vec3 v : a) {
            x = Math.min(x, v.fineX());
            y = Math.min(y, v.fineY());
            z = Math.min(z, v.fineZ());
        }
        return of(x, y, z);
    }

    public static ImVector max(Vec3... a) {
        double x, y, z;
        x = y = z = Double.NEGATIVE_INFINITY;
        for(Vec3 v : a) {
            x = Math.max(x, v.fineX());
            y = Math.max(y, v.fineY());
            z = Math.max(z, v.fineZ());
        }
        return of(x, y, z);
    }

    public static ImVector interpolate(Vec3 a, Vec3 b, double n) {
        return of(NumberConversions.interpolate(a.fineX(), b.fineX(), n),
                  NumberConversions.interpolate(a.fineY(), b.fineY(), n),
                  NumberConversions.interpolate(a.fineZ(), b.fineZ(), n));
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public ImVector copy() {
        return this;
    }

    @Override
    public ImVector times(int n) {
        if(n == 0) return ZERO;
        if(n == 1) return this;
        return super.times(n);
    }

    @Override
    public ImVector times(double n) {
        if(n == 0) return ZERO;
        if(n == 1) return this;
        return super.times(n);
    }

    @Override
    public ImVector over(double n) {
        if(Double.isInfinite(n)) {
            return ZERO;
        } else if(n == 1) {
            return this;
        } else {
            return super.over(n);
        }
    }

    @Override
    public ImVector getCrossProduct(Vector v) {
        return of(this.fineY() * v.fineZ() - v.fineY() * this.fineZ(),
                  this.fineZ() * v.fineX() - v.fineZ() * this.fineX(),
                  this.fineX() * v.fineY() - v.fineX() * this.fineY());
    }

    @Override
    public ImVector getMidpoint(Vector v) {
        return of((fineX() + v.getX()) / 2,
                  (fineY() + v.getY()) / 2,
                  (fineZ() + v.getZ()) / 2);
    }

    private UnsupportedOperationException ex() {
        return new UnsupportedOperationException("object is immutable");
    }

    @Override public Vector setX(double x) { throw ex(); }
    @Override public Vector setY(double y) { throw ex(); }
    @Override public Vector setZ(double z) { throw ex(); }
    @Override public Vector set(double x, double y, double z) { throw ex(); }
}
