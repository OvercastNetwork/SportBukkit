package org.bukkit.geometry;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * Extends {@link Vec3} with mutating methods.
 *
 * All mutating methods return the vector they are called on.
 *
 * Due to the sordid history of Bukkit vector types, some implementors
 * of this interface are NOT, in fact, mutable. However, all vectors that
 * are mutable do implement this interface.
 *
 * @see MutableVec3Coarse
 * @see MutableVec3Fine
 */
public interface MutableVec3 extends Vec3 {

    /**
     * Return an unmodifiable view of this vector
     */
    Vec3 unmodifiable();

    MutableVec3 setX(double x);
    MutableVec3 setY(double y);
    MutableVec3 setZ(double z);

    MutableVec3 setX(int x);
    MutableVec3 setY(int y);
    MutableVec3 setZ(int z);

    MutableVec3 set(int x, int y, int z);
    MutableVec3 set(double x, double y, double z);
    MutableVec3 set(Vec3 v);

    MutableVec3 setZero();

    MutableVec3 modify(IntUnaryOperator op);
    MutableVec3 modify(DoubleUnaryOperator op);
    MutableVec3 modify(Vec3 v, IntBinaryOperator op);
    MutableVec3 modify(Vec3 v, DoubleBinaryOperator op);

    MutableVec3 add(int x, int y, int z);
    MutableVec3 add(double x, double y, double z);
    MutableVec3 add(Vec3 v);

    MutableVec3 subtract(int x, int y, int z);
    MutableVec3 subtract(double x, double y, double z);
    MutableVec3 subtract(Vec3 v);

    MutableVec3 multiply(int x, int y, int z);
    MutableVec3 multiply(double x, double y, double z);
    MutableVec3 multiply(Vec3 v);

    MutableVec3 divide(int x, int y, int z);
    MutableVec3 divide(double x, double y, double z);
    MutableVec3 divide(Vec3 v);

    MutableVec3 minimize(Vec3 v);
    MutableVec3 maximize(Vec3 v);
    MutableVec3 clamp(Vec3 min, Vec3 max);

    default MutableVec3 set(Axis axis, double n) {
        switch(axis) {
            case X: return setX(n);
            case Y: return setY(n);
            case Z: return setZ(n);
        }
        throw new IllegalStateException();
    }

    default MutableVec3 set(Axis axis, int n) {
        switch(axis) {
            case X: return setX(n);
            case Y: return setY(n);
            case Z: return setZ(n);
        }
        throw new IllegalStateException();
    }

    default MutableVec3 set(int xyz) {
        return set(xyz, xyz, xyz);
    }

    default MutableVec3 set(double xyz) {
        return set(xyz, xyz, xyz);
    }

    default MutableVec3 add(int xyz) {
        return add(xyz, xyz, xyz);
    }

    default MutableVec3 add(double xyz) {
        return add(xyz, xyz, xyz);
    }

    default MutableVec3 subtract(int xyz) {
        return subtract(xyz, xyz, xyz);
    }

    default MutableVec3 subtract(double xyz) {
        return subtract(xyz, xyz, xyz);
    }

    default MutableVec3 multiply(int n) {
        return multiply(n, n, n);
    }

    default MutableVec3 multiply(double n) {
        return multiply(n, n, n);
    }

    default MutableVec3 divide(int n) {
        return divide(n, n, n);
    }

    default MutableVec3 divide(double n) {
        return divide(n, n, n);
    }
}
