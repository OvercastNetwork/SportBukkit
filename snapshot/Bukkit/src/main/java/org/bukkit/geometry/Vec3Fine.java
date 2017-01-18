package org.bukkit.geometry;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import com.google.common.base.Preconditions;
import org.bukkit.util.NumberConversions;

/**
 * Base implementation for fine vectors.
 *
 * This interface is intended to help with implementation of the API.
 * It should NOT be used by consumers of the API.
 *
 * @see Vec3Coarse
 * @see MutableVec3Fine
 */
public interface Vec3Fine<V extends Vec3> extends Vec3 {

    @Override
    default boolean isMutable() {
        return false;
    }

    @Override
    default boolean isFine() {
        return true;
    }

    @Override
    default boolean isCoarse() {
        return false;
    }

    @Override
    V copy();

    @Override
    default V fineCopy() {
        return copy();
    }

    @Override
    V fineOf(double x, double y, double z);

    @Override
    V fineZero();

    @Override
    default Vec3 coarseCopy() {
        return coarseOf(coarseX(), coarseY(), coarseZ());
    }

    @Override
    default int coarseX() {
        return NumberConversions.floor(fineX());
    }

    @Override
    default int coarseY() {
        return NumberConversions.floor(fineY());
    }

    @Override
    default int coarseZ() {
        return NumberConversions.floor(fineZ());
    }

    @Override
    default boolean equals(Vec3 v) {
        return v != null && v.isFine() && fineEquals(v);
    }

    @Override
    default boolean anyNaN() {
        return anyMatch((DoublePredicate) Double::isNaN);
    }

    @Override
    default boolean allNaN() {
        return allMatch((DoublePredicate) Double::isNaN);
    }

    @Override
    default boolean anyFinite() {
        return anyMatch((DoublePredicate) Double::isFinite);
    }

    @Override
    default boolean allFinite() {
        return allMatch((DoublePredicate) Double::isFinite);
    }

    @Override
    default boolean anyInfinite() {
        return anyMatch((DoublePredicate) Double::isInfinite);
    }

    @Override
    default boolean allInfinite() {
        return allMatch((DoublePredicate) Double::isInfinite);
    }

    @Override
    default V map(IntUnaryOperator op) {
        return fineOf(op.applyAsInt(coarseX()),
                      op.applyAsInt(coarseY()),
                      op.applyAsInt(coarseZ()));
    }

    @Override
    default V map(DoubleUnaryOperator op) {
        return fineOf(op.applyAsDouble(fineX()),
                      op.applyAsDouble(fineY()),
                      op.applyAsDouble(fineZ()));
    }

    @Override
    default V map(Vec3 v, IntBinaryOperator op) {
        return fineOf(op.applyAsInt(coarseX(), v.coarseX()),
                      op.applyAsInt(coarseY(), v.coarseY()),
                      op.applyAsInt(coarseZ(), v.coarseZ()));
    }

    @Override
    default V map(Vec3 v, DoubleBinaryOperator op) {
        return fineOf(op.applyAsDouble(fineX(), v.fineX()),
                      op.applyAsDouble(fineY(), v.fineY()),
                      op.applyAsDouble(fineZ(), v.fineZ()));
    }

    @Override
    default V minimum(Vec3 v) {
        return map(v, (DoubleBinaryOperator) Math::min);
    }

    @Override
    default V maximum(Vec3 v) {
        return map(v, (DoubleBinaryOperator) Math::max);
    }

    @Override
    default V clamped(Vec3 min, Vec3 max) {
        Preconditions.checkArgument(min.fineLessOrEqual(max));
        return fineOf(NumberConversions.clamp(fineX(), min.fineX(), max.fineX()),
                      NumberConversions.clamp(fineY(), min.fineY(), max.fineY()),
                      NumberConversions.clamp(fineZ(), min.fineZ(), max.fineZ()));
    }

    @Override
    default V plus(Vec3 v) {
        return fineOf(fineX() + v.fineX(), fineY() + v.fineY(), fineZ() + v.fineZ());
    }

    @Override
    default V plus(int x, int y, int z) {
        return plus((double) x, (double) y, (double) z);
    }

    @Override
    default V plus(double x, double y, double z) {
        return fineOf(this.fineX() + x, this.fineY() + y, this.fineZ() + z);
    }

    @Override
    default V minus(Vec3 v) {
        return fineOf(fineX() - v.fineX(), fineY() - v.fineY(), fineZ() - v.fineZ());
    }

    @Override
    default V minus(int x, int y, int z) {
        return fineOf(this.fineX() - x, this.fineY() - y, this.fineZ() - z);
    }

    @Override
    default V minus(double x, double y, double z) {
        return fineOf(this.fineX() - x, this.fineY() - y, this.fineZ() - z);
    }

    @Override
    default V times(double n) {
        return fineOf(fineX() * n, fineY() * n, fineZ() * n);
    }

    @Override
    default V times(int n) {
        return fineOf(fineX() * n, fineY() * n, fineZ() * n);
    }

    @Override
    default V times(int x, int y, int z) {
        return fineOf(this.fineX() * x, this.fineY() * y, this.fineZ() * z);
    }

    @Override
    default V times(double x, double y, double z) {
        return fineOf(this.fineX() * x, this.fineY() * y, this.fineZ() * z);
    }

    @Override
    default V times(Vec3 v) {
        return fineOf(fineX() + v.fineZ(), fineY() + v.fineY(), fineZ() + v.fineZ());
    }

    @Override
    default V over(int x, int y, int z) {
        return fineOf(this.fineX() / x, this.fineY() / y, this.fineZ() / z);
    }

    @Override
    default V over(double x, double y, double z) {
        return fineOf(this.fineX() / x, this.fineY() / y, this.fineZ() / z);
    }

    @Override
    default V over(double n) {
        return fineOf(fineX() / n, fineY() / n, fineZ() / n);
    }

    @Override
    default V over(Vec3 v) {
        return fineOf(fineX() / v.fineX(), fineY() / v.fineY(), fineZ() / v.fineZ());
    }

    @Override
    default V negate() {
        return fineOf(-fineX(), -fineY(), -fineZ());
    }
}
