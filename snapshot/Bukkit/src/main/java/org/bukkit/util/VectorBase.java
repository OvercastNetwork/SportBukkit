package org.bukkit.util;

import org.bukkit.geometry.MutableVec3Fine;

abstract class VectorBase<M extends VectorBase<M>> implements MutableVec3Fine<ImVector, M> {

    private double x;
    private double y;
    private double z;

    protected VectorBase(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double fineX() {
        return x;
    }

    @Override
    public double fineY() {
        return y;
    }

    @Override
    public double fineZ() {
        return z;
    }

    @Override
    public M setX(double x) {
        this.x = x;
        return (M) this;
    }

    @Override
    public M setY(double y) {
        this.y = y;
        return (M) this;
    }

    @Override
    public M setZ(double z) {
        this.z = z;
        return (M) this;
    }

    @Override
    public M set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return (M) this;
    }
}
