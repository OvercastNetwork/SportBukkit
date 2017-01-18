package org.bukkit.geometry;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import com.google.common.base.Preconditions;
import org.bukkit.util.NumberConversions;

/**
 * Base implementation for mutable coarse vectors.
 *
 * This interface is intended to help with implementation of the API.
 * It should NOT be used by consumers of the API.
 *
 * @see MutableVec3Fine
 */
public interface MutableVec3Coarse<V extends Vec3, M extends MutableVec3Coarse<V, M>> extends MutableVec3, Vec3Coarse<V> {

    @Override M setX(int x);
    @Override M setY(int y);
    @Override M setZ(int z);

    @Override
    M set(int x, int y, int z);

    @Override
    default boolean isMutable() {
        return true;
    }

    @Override
    default V copy() {
        return coarseOf(coarseX(), coarseY(), coarseZ());
    }

    @Override
    default M setX(double x) {
        return setX(NumberConversions.floor(x));
    }

    @Override
    default M setY(double y) {
        return setY(NumberConversions.floor(y));
    }

    @Override
    default M setZ(double z) {
        return setZ(NumberConversions.floor(z));
    }

    @Override
    default M setZero() {
        return set(0, 0, 0);
    }

    @Override
    default M set(double x, double y, double z) {
        return set(NumberConversions.floor(x),
                   NumberConversions.floor(y),
                   NumberConversions.floor(z));
    }

    @Override
    default M set(Vec3 v) {
        return set(v.coarseX(),
                   v.coarseY(),
                   v.coarseZ());
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
        return modify(v, (IntBinaryOperator) Math::min);
    }

    @Override
    default M maximize(Vec3 v) {
        return modify(v, (IntBinaryOperator) Math::max);
    }

    @Override
    default M clamp(Vec3 min, Vec3 max) {
        Preconditions.checkArgument(min.coarseLessOrEqual(max));
        return set(NumberConversions.clamp(coarseX(), min.coarseX(), max.coarseX()),
                   NumberConversions.clamp(coarseY(), min.coarseY(), max.coarseY()),
                   NumberConversions.clamp(coarseZ(), min.coarseZ(), max.coarseZ()));
    }

    @Override
    default M add(int x, int y, int z) {
        return set(coarseX() + x,
                   coarseY() + y,
                   coarseZ() + z);
    }

    @Override
    default M add(double x, double y, double z) {
        return add(NumberConversions.floor(x),
                   NumberConversions.floor(y),
                   NumberConversions.floor(z));
    }

    @Override
    default M add(Vec3 v) {
        return add(v.coarseX(),
                   v.coarseY(),
                   v.coarseZ());
    }

    @Override
    default M subtract(double x, double y, double z) {
        // Be careful to floor AFTER negating
        return add(-x, -y, -z);
    }

    @Override
    default M subtract(int x, int y, int z) {
        return set(coarseX() - x,
                   coarseY() - y,
                   coarseZ() - z);
    }

    @Override
    default M subtract(Vec3 v) {
        return v.isFine() ? subtract(v.fineX(), v.fineY(), v.fineZ())
                          : subtract(v.coarseX(), v.coarseY(), v.coarseZ());
    }

    @Override
    default M multiply(int x, int y, int z) {
        return set(coarseX() * x, coarseY() * y, coarseZ() * z);
    }

    @Override
    default M multiply(double x, double y, double z) {
        return set(coarseX() * x, coarseY() * y, coarseZ() * z);
    }

    @Override
    default M multiply(Vec3 v) {
        return v.isFine() ? multiply(v.fineX(), v.fineY(), v.fineZ())
                          : multiply(v.coarseX(), v.coarseY(), v.coarseZ());
    }

    @Override
    default M divide(int x, int y, int z) {
        return set(coarseX() / x, coarseY() / y, coarseZ() / z);
    }

    @Override
    default M divide(double x, double y, double z) {
        return set(coarseX() / x, coarseY() / y, coarseZ() / z);
    }

    @Override
    default M divide(Vec3 v) {
        return v.isFine() ? divide(v.fineX(), v.fineY(), v.fineZ())
                          : divide(v.coarseX(), v.coarseY(), v.coarseZ());
    }
}
