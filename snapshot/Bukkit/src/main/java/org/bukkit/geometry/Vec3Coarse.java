package org.bukkit.geometry;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import com.google.common.base.Preconditions;
import org.bukkit.util.NumberConversions;

/**
 * Base implementation for coarse vectors.
 *
 * This interface is intended to help with implementation of the API.
 * It should NOT be used by consumers of the API.
 *
 * @see Vec3Fine
 * @see MutableVec3Coarse
 */
public interface Vec3Coarse<V extends Vec3> extends Vec3 {

    @Override
    default boolean isMutable() {
        return false;
    }

    @Override
    default boolean isFine() {
        return false;
    }

    @Override
    default boolean isCoarse() {
        return true;
    }

    @Override
    V copy();

    @Override
    default V coarseCopy() {
        return copy();
    }

    @Override
    V coarseOf(int x, int y, int z);

    @Override
    V coarseZero();

    default V coarseOf(double x, double y, double z) {
        return coarseOf(NumberConversions.floor(x),
                        NumberConversions.floor(y),
                        NumberConversions.floor(z));
    }

    @Override
    default double fineX() {
        return coarseX();
    }

    @Override
    default double fineY() {
        return coarseY();
    }

    @Override
    default double fineZ() {
        return coarseZ();
    }

    @Override
    default boolean equals(Vec3 v) {
        return v != null && v.isCoarse() && coarseEquals(v);
    }

    @Override
    default boolean fineLess(Vec3 v) {
        return v.isCoarse() ? coarseLess(v)
                            : Vec3.super.fineLess(v);
    }

    @Override
    default boolean fineLessOrEqual(Vec3 v) {
        return v.isCoarse() ? coarseLessOrEqual(v)
                            : Vec3.super.fineLessOrEqual(v);
    }

    @Override
    default boolean fineGreater(Vec3 v) {
        return v.isCoarse() ? coarseGreater(v)
                            : Vec3.super.fineGreater(v);
    }

    @Override
    default boolean fineGreaterOrEqual(Vec3 v) {
        return v.isCoarse() ? coarseGreaterOrEqual(v)
                            : Vec3.super.fineGreaterOrEqual(v);
    }

    @Override
    default boolean anyNaN() {
        return false;
    }

    @Override
    default boolean allNaN() {
        return false;
    }

    @Override
    default boolean anyFinite() {
        return true;
    }

    @Override
    default boolean allFinite() {
        return true;
    }

    @Override
    default boolean anyInfinite() {
        return false;
    }

    @Override
    default boolean allInfinite() {
        return false;
    }

    @Override
    default V map(IntUnaryOperator op) {
        return coarseOf(op.applyAsInt(coarseX()),
                        op.applyAsInt(coarseY()),
                        op.applyAsInt(coarseZ()));
    }

    @Override
    default V map(DoubleUnaryOperator op) {
        return coarseOf(op.applyAsDouble(fineX()),
                        op.applyAsDouble(fineY()),
                        op.applyAsDouble(fineZ()));
    }

    @Override
    default V map(Vec3 v, IntBinaryOperator op) {
        return coarseOf(op.applyAsInt(coarseX(), v.coarseX()),
                        op.applyAsInt(coarseY(), v.coarseY()),
                        op.applyAsInt(coarseZ(), v.coarseZ()));
    }

    @Override
    default V map(Vec3 v, DoubleBinaryOperator op) {
        return coarseOf(op.applyAsDouble(fineX(), v.fineX()),
                        op.applyAsDouble(fineY(), v.fineY()),
                        op.applyAsDouble(fineZ(), v.fineZ()));
    }

    @Override
    default V minimum(Vec3 v) {
        return map(v, (IntBinaryOperator) Math::min);
    }

    @Override
    default V maximum(Vec3 v) {
        return map(v, (IntBinaryOperator) Math::max);
    }

    @Override
    default V clamped(Vec3 min, Vec3 max) {
        Preconditions.checkArgument(min.coarseLessOrEqual(max));
        return coarseOf(NumberConversions.clamp(coarseX(), min.coarseX(), max.coarseX()),
                        NumberConversions.clamp(coarseY(), min.coarseY(), max.coarseY()),
                        NumberConversions.clamp(coarseZ(), min.coarseZ(), max.coarseZ()));
    }

    @Override
    default V negate() {
        return isZero() ? copy() : coarseOf(-coarseX(), -coarseY(), -coarseZ());
    }

    @Override
    default V plus(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0
               ? copy()
               : coarseOf(coarseX() + x, coarseY() + y, coarseZ() + z);
    }

    @Override
    default V plus(double x, double y, double z) {
        return x == 0D && y == 0D && z == 0D
               ? copy()
               : coarseOf(coarseX() + x, coarseY() + y, coarseZ() + z);
    }

    @Override
    default V plus(Vec3 v) {
        return plus(v.coarseX(), v.coarseY(), v.coarseZ());
    }

    @Override
    default V minus(int x, int y, int z) {
        return plus(-x, -y, -z);
    }

    @Override
    default V minus(double x, double y, double z) {
        return plus(-x, -y, -z);
    }

    @Override
    default V minus(Vec3 v) {
        return minus(v.coarseX(), v.coarseY(), v.coarseZ());
    }

    @Override
    default V times(int n) {
        if(n == 0 || isCoarseZero()) return coarseZero();
        return coarseOf(coarseX() * n, coarseY() * n, coarseZ() * n);
    }

    @Override
    default V times(double n) {
        if(n == 0D || isCoarseZero()) return coarseZero();
        return coarseOf(coarseX() * n, coarseY() * n, coarseZ() * n);
    }

    @Override
    default V times(int x, int y, int z) {
        if((x == 0 && y == 0 && z == 0) || isCoarseZero()) return coarseZero();
        if(x == 1 && y == 1 && z == 1) return copy();
        return coarseOf(coarseX() * x, coarseY() * y, coarseZ() * z);
    }

    @Override
    default V times(double x, double y, double z) {
        if((x == 0D && y == 0D && z == 0D) || isCoarseZero()) return coarseZero();
        if(x == 1D && y == 1D && z == 1D) return copy();
        return coarseOf(coarseX() * x, coarseY() * y, coarseZ() * z);
    }

    @Override
    default V times(Vec3 v) {
        if(v.isZero() || isCoarseZero()) return coarseZero();
        return v.isFine() ? coarseOf(coarseX() * v.fineX(), coarseY() * v.fineY(), coarseZ() * v.fineZ())
                          : coarseOf(coarseX() * v.coarseX(), coarseY() * v.coarseY(), coarseZ() * v.coarseZ());
    }

    @Override
    default V over(int x, int y, int z) {
        if(x == 1 && y == 1 && z == 1) return copy();
        return coarseOf(coarseX() / x, coarseY() / y, coarseZ() / z);
    }

    @Override
    default V over(double x, double y, double z) {
        if(x == 1 && y == 1 && z == 1) return copy();
        if(Double.isInfinite(x) && Double.isInfinite(y) && Double.isInfinite(z)) return coarseZero();
        return coarseOf(coarseX() / x, coarseY() / y, coarseZ() / z);
    }

    @Override
    default V over(Vec3 v) {
        return v.isFine() ? coarseOf(coarseX() / v.fineX(), coarseY() / v.fineY(), coarseZ() / v.fineZ())
                          : coarseOf(coarseX() / v.coarseX(), coarseY() / v.coarseY(), coarseZ() / v.coarseZ());
    }
}
