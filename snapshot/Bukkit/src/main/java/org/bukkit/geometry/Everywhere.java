package org.bukkit.geometry;

import java.util.Random;

import org.bukkit.region.BlockRegion;

class Everywhere implements Region {

    static final Everywhere INSTANCE = new Everywhere();

    private Everywhere() {}

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isBlockEmpty() {
        return false;
    }

    @Override
    public boolean isFinite() {
        return false;
    }

    @Override
    public boolean isBlockFinite() {
        return false;
    }

    @Override
    public double volume() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public int blockVolume() throws ArithmeticException {
        assertBlockFinite();
        return 0;
    }

    @Override
    public boolean contains(Vec3 point) {
        return true;
    }

    @Override
    public boolean containsBlock(Vec3 v) {
        return true;
    }

    @Override
    public Vec3 randomPointInside(Random random) throws ArithmeticException {
        assertFinite();
        return null;
    }

    @Override
    public Vec3 randomBlockInside(Random random) throws ArithmeticException {
        assertBlockFinite();
        return null;
    }

    @Override
    public Cuboid bounds() {
        return Cuboid.unbounded();
    }

    @Override
    public BlockRegion blockRegion() throws ArithmeticException {
        assertBlockFinite();
        return null;
    }

    @Override
    public Region transform(Transform transform) {
        return this;
    }
}
