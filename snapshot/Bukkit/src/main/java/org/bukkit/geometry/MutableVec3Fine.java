package org.bukkit.geometry;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import com.google.common.base.Preconditions;
import org.bukkit.util.NumberConversions;

/**
 * Base implementation for mutable fine vectors.
 *
 * This interface is intended to help with implementation of the API.
 * It should NOT be used by consumers of the API.
 *
 * @see MutableVec3Coarse
 */
public interface MutableVec3Fine<V extends Vec3, M extends MutableVec3Fine<V, M>> extends MutableVec3, Vec3Fine<V> {

    @Override M setX(double x);
    @Override M setY(double y);
    @Override M setZ(double z);

    @Override
    M set(double x, double y, double z);

    @Override
    default boolean isMutable() {
        return true;
    }

    @Override
    default V copy() {
        return fineOf(fineX(), fineY(), fineZ());
    }

    @Override
    default M setZero() {
        return set(0, 0, 0);
    }

    @Override
    default M setX(int x) {
        return setX((double) x);
    }

    @Override
    default M setY(int y) {
        return setX((double) y);
    }

    @Override
    default M setZ(int z) {
        return setX((double) z);
    }

    @Override
    default M set(int x, int y, int z) {
        return set((double) x, (double) y, (double) z);
    }

    @Override
    default M set(Vec3 v) {
        return set(v.fineX(), v.fineY(), v.fineZ());
    }

    @Override
    default M modify(IntUnaryOperator op) {
        return set(op.applyAsInt(coarseX()),
                   op.applyAsInt(coarseY()),
                   op.applyAsInt(coarseZ()));
    }

    @Override
    default M modify(DoubleUnaryOperator op) {
        return set(op.applyAsDouble(fineX()),
                   op.applyAsDouble(fineY()),
                   op.applyAsDouble(fineZ()));
    }

    @Override
    default M modify(Vec3 v, IntBinaryOperator op) {
        return set(op.applyAsInt(coarseX(), v.coarseX()),
                   op.applyAsInt(coarseY(), v.coarseY()),
                   op.applyAsInt(coarseZ(), v.coarseZ()));
    }

    @Override
    default M modify(Vec3 v, DoubleBinaryOperator op) {
        return set(op.applyAsDouble(fineX(), v.fineX()),
                   op.applyAsDouble(fineY(), v.fineY()),
                   op.applyAsDouble(fineZ(), v.fineZ()));
    }

    @Override
    default M minimize(Vec3 v) {
        return set(Math.min(fineX(), v.fineX()),
                   Math.min(fineY(), v.fineY()),
                   Math.min(fineZ(), v.fineZ()));
    }

    @Override
    default M maximize(Vec3 v) {
        return set(Math.max(fineX(), v.fineX()),
                   Math.max(fineY(), v.fineY()),
                   Math.max(fineZ(), v.fineZ()));
    }

    @Override
    default M clamp(Vec3 min, Vec3 max) {
        Preconditions.checkArgument(min.fineLessOrEqual(max));
        return set(NumberConversions.clamp(fineX(), min.fineX(), max.fineX()),
                   NumberConversions.clamp(fineY(), min.fineY(), max.fineY()),
                   NumberConversions.clamp(fineZ(), min.fineZ(), max.fineZ()));
    }

    @Override
    default M add(int x, int y, int z) {
        return add((double) x, (double) y, (double) z);
    }

    @Override
    default M add(double x, double y, double z) {
        return set(fineX() + x, fineY() + y, fineZ() + z);
    }

    @Override
    default M add(Vec3 v) {
        return add(v.fineX(), v.fineY(), v.fineZ());
    }

    @Override
    default M subtract(int x, int y, int z) {
        return subtract((double) x, (double) y, (double) z);
    }

    @Override
    default M subtract(double x, double y, double z) {
        return set(fineX() - x, fineY() - y, fineZ() - z);
    }

    @Override
    default M subtract(Vec3 v) {
        return subtract(v.fineX(), v.fineY(), v.fineZ());
    }

    @Override
    default M multiply(int x, int y, int z) {
        return set(fineX() * x, fineY() * y, fineZ() * z);
    }

    @Override
    default M multiply(double x, double y, double z) {
        return set(fineX() * x, fineY() * y, fineZ() * z);
    }

    @Override
    default M multiply(Vec3 v) {
        return multiply(v.fineX(), v.fineY(), v.fineZ());
    }

    @Override
    default M divide(int x, int y, int z) {
        return set(fineX() / x, fineY() / y, fineZ() / z);
    }

    @Override
    default M divide(double x, double y, double z) {
        return set(fineX() / x, fineY() / y, fineZ() / z);
    }

    @Override
    default M divide(Vec3 v) {
        return divide(v.fineX(), v.fineY(), v.fineZ());
    }
}
