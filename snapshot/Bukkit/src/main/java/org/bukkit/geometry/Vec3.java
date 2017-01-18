package org.bukkit.geometry;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.bukkit.util.NumberConversions;

/**
 * Base interface for all 3-vector types.
 *
 * Subtypes may have either coarse (integer) resolution, or fine (real) resolution,
 * which can be checked with {@link #isCoarse()} or {@link #isFine()}. Regardless of resolution,
 * the element values are always available as either integers or doubles. For coarse vectors,
 * the fine components are the closest double approximations of the coarse components. For fine
 * vectors, the coarse components are the floors of the fine components.
 *
 * Coarse vectors compare equal if and only if their integer components are all equal.
 * Fine vectors compare equal if and only if their fine components are all EXACTLY equal.
 * Coarse and fine vectors NEVER compare equal to each other, even if they have equal components.
 *
 * These rules for (in)equality apply to ALL {@link Vec3} implementations, and any given implementation
 * must be comparable to any other implementation, and generate identical {@link #hashCode}s.
 * The methods {@link #fineHashCode()}, {@link #coarseHashCode()}, and {@link #equals(Vec3)} can be used to
 * easily implement this.
 *
 * Note that coarse vectors MAY be able to store and retrieve fractional coordinates,
 * but they must use only integer coordinates when comparing to other vectors or generating hash codes.
 *
 * Generally, methods that return a {@link Vec3} result will return a vector with the same resolution as the one
 * the method is called on. Binary operations between a coarse and fine vector will (effectively) use the
 * fine components of both vectors, and the result will be converted to a coarse vector if necessary.
 * There are a few noted exceptions to this rule, such as {@link #unit()}, which always returns a fine vector.
 *
 * Vectors are generally immutable, and the operations defined in the {@link Vec3} interface ALWAYS return
 * immutable vectors that are independent of the original vector (with a few obvious exceptions, such as
 * {@link #mutableCopy()}. Mutable vectors implement {@link MutableVec3}, which is where all the mutating
 * operations are defined.
 *
 * @see MutableVec3
 * @see Vec3Coarse
 * @see Vec3Fine
 * @see VectorFactory
 */
public interface Vec3 {

    /**
     * Can the value of this vector change?
     */
    boolean isMutable();

    /**
     * Can this vector contain fractional coordinates?
     */
    boolean isFine();

    /**
     * Inverse of {@link #isFine()}
     */
    default boolean isCoarse() {
        return !isFine();
    }

    /**
     * Return the ith fine component of the vector, starting at 0
     *
     * @throws IndexOutOfBoundsException if there is no component with the given index
     */
    default double fineAt(int i) {
        switch(i) {
            case 0: return fineX();
            case 1: return fineY();
            case 2: return fineZ();
        }
        throw new IndexOutOfBoundsException();
    }

    default double fineAt(Axis axis) {
        switch(axis) {
            case X: return fineX();
            case Y: return fineY();
            case Z: return fineZ();
        }
        throw new IllegalStateException();
    }

    double fineX();

    double fineY();

    double fineZ();

    default DoubleStream fineStream() {
        return DoubleStream.of(fineX(), fineY(), fineZ());
    }

    default boolean isZero() {
        return fineX() == 0 && fineY() == 0 && fineZ() == 0;
    }

    /**
     * Return the ith coarse component of the vector, starting at 0
     *
     * @throws IndexOutOfBoundsException if there is no component with the given index
     */
    default int coarseAt(int i) {
        switch(i) {
            case 0: return coarseX();
            case 1: return coarseY();
            case 2: return coarseZ();
        }
        throw new IndexOutOfBoundsException();
    }

    default int coarseAt(Axis axis) {
        switch(axis) {
            case X: return coarseX();
            case Y: return coarseY();
            case Z: return coarseZ();
        }
        throw new IllegalStateException();
    }

    int coarseX();

    int coarseY();

    int coarseZ();

    default IntStream coarseStream() {
        return IntStream.of(coarseX(), coarseY(), coarseZ());
    }

    /**
     * Are the integer components of this object all zero?
     */
    default boolean isCoarseZero() {
        return coarseX() == 0 && coarseY() == 0 && coarseZ() == 0;
    }

    default boolean fineEquals(Vec3 v) {
        return v != null &&
               fineX() == v.fineX() &&
               fineY() == v.fineY() &&
               fineZ() == v.fineZ();
    }

    default boolean coarseEquals(Vec3 v) {
        return v != null &&
               coarseX() == v.coarseX() &&
               coarseY() == v.coarseY() &&
               coarseZ() == v.coarseZ();
    }

    /**
     * Test if this vector is equal to the given vector.
     *
     * See the notes about equality in the {@link Vec3} documentation.
     */
    default boolean equals(Vec3 v) {
        if(v == null) return false;

        if(isFine()) {
            return v.isFine() && fineEquals(v);
        } else {
            return v.isCoarse() && coarseEquals(v);
        }
    }

    /**
     * Return the proper hashCode of a vector whos individual components have the given hashCodes
     *
     * This is intended for use by implementors of this interface
     */
    static int combineHashCodes(int hashX, int hashY, int hashZ) {
        // 31 bits gives us roughly 10 bits per axis, and 1021 is the closest prime to 2^10
        return (hashX * 1021 + hashY) * 1021 + hashZ;
    }

    default int fineHashCode() {
        return combineHashCodes(
            NumberConversions.hashCode(fineX()),
            NumberConversions.hashCode(fineY()),
            NumberConversions.hashCode(fineZ())
        );
    }

    default int coarseHashCode() {
        return combineHashCodes(
            coarseX(),
            coarseY(),
            coarseZ()
        );
    }

    /**
     * Return an immutable vector equal to the current value of this vector.
     * If this vector is immutable, it may return itself.
     */
    Vec3 copy();

    /**
     * Return a new mutable vector equal to the current value of this vector.
     * This is always a new object that is independent of this vector.
     */
    MutableVec3 mutableCopy();

    /**
     * Return an immutable fine vector with components equal to the fine
     * components of this vector.
     */
    Vec3 fineCopy();

    Vec3 fineOf(double x, double y, double z);

    Vec3 fineZero();

    /**
     * Return an immutable coarse vector with components equal to the integer
     * components of this vector.
     */
    Vec3 coarseCopy();

    Vec3 coarseOf(int x, int y, int z);

    Vec3 coarseZero();

    /**
     * Return true only if ALL fine components of this vector are strictly less
     * than their respective fine component in the given vector.
     */
    default boolean fineLess(Vec3 v) {
        return fineX() < v.fineX() &&
               fineY() < v.fineY() &&
               fineZ() < v.fineZ();
    }

    /**
     * Return true only if ALL fine components of this vector are less or equal
     * to their respective fine component in the given vector.
     */
    default boolean fineLessOrEqual(Vec3 v) {
        return fineX() <= v.fineX() &&
               fineY() <= v.fineY() &&
               fineZ() <= v.fineZ();
    }

    /**
     * Return true only if ALL fine components of this vector are strictly greater
     * than their respective fine component in the given vector.
     */
    default boolean fineGreater(Vec3 v) {
        return fineX() > v.fineX() &&
               fineY() > v.fineY() &&
               fineZ() > v.fineZ();
    }

    /**
     * Return true only if ALL fine components of this vector are greater or equal
     * to their respective fine component in the given vector.
     */
    default boolean fineGreaterOrEqual(Vec3 v) {
        return fineX() >= v.fineX() &&
               fineY() >= v.fineY() &&
               fineZ() >= v.fineZ();
    }

    /**
     * Return true only if ALL coarse components of this vector are strictly less
     * than their respective coarse component in the given vector.
     */
    default boolean coarseLess(Vec3 v) {
        return coarseX() < v.coarseX() &&
               coarseY() < v.coarseY() &&
               coarseZ() < v.coarseZ();
    }

    /**
     * Return true only if ALL coarse components of this vector are less or equal
     * to their respective coarse component in the given vector.
     */
    default boolean coarseLessOrEqual(Vec3 v) {
        return coarseX() <= v.coarseX() &&
               coarseY() <= v.coarseY() &&
               coarseZ() <= v.coarseZ();
    }

    /**
     * Return true only if ALL coarse components of this vector are strictly greater
     * than their respective coarse component in the given vector.
     */
    default boolean coarseGreater(Vec3 v) {
        return coarseX() > v.coarseX() &&
               coarseY() > v.coarseY() &&
               coarseZ() > v.coarseZ();
    }

    /**
     * Return true only if ALL coarse components of this vector are greater or equal
     * to their respective coarse component in the given vector.
     */
    default boolean coarseGreaterOrEqual(Vec3 v) {
        return coarseX() >= v.coarseX() &&
               coarseY() >= v.coarseY() &&
               coarseZ() >= v.coarseZ();
    }

    default boolean isBlockCorner() {
        return isFine() &&
               fineX() == coarseX() &&
               fineY() == coarseY() &&
               fineZ() == coarseZ();
    }

    default boolean isBlockCenter() {
        return isFine() &&
               fineX() == coarseX() + 0.5 &&
               fineY() == coarseY() + 0.5 &&
               fineZ() == coarseZ() + 0.5;
    }

    /**
     * Return an immutable fine vector at the center of the block position
     * represented by this vector's coarse coordinates.
     */
    default Vec3 blockCenter() {
        return fineOf(coarseX() + 0.5,
                      coarseY() + 0.5,
                      coarseZ() + 0.5);
    }

    default boolean anyMatch(DoublePredicate predicate) {
        return predicate.test(fineX()) ||
               predicate.test(fineY()) ||
               predicate.test(fineZ());
    }

    default boolean anyMatch(IntPredicate predicate) {
        return predicate.test(coarseX()) ||
               predicate.test(coarseY()) ||
               predicate.test(coarseZ());
    }

    default boolean allMatch(DoublePredicate predicate) {
        return predicate.test(fineX()) &&
               predicate.test(fineY()) &&
               predicate.test(fineZ());
    }

    default boolean allMatch(IntPredicate predicate) {
        return predicate.test(coarseX()) &&
               predicate.test(coarseY()) &&
               predicate.test(coarseZ());
    }

    boolean anyNaN();
    boolean allNaN();
    boolean anyFinite();
    boolean allFinite();
    boolean anyInfinite();
    boolean allInfinite();

    Vec3 map(IntUnaryOperator op);

    Vec3 map(DoubleUnaryOperator op);

    Vec3 map(Vec3 v, IntBinaryOperator op);

    Vec3 map(Vec3 v, DoubleBinaryOperator op);

    /**
     * Return a vector of the minimums of each component of this vector
     * and the respective component of the given vector.
     */
    Vec3 minimum(Vec3 v);

    /**
     * Return a vector of the maximums of each component of this vector
     * and the respective component of the given vector.
     */
    Vec3 maximum(Vec3 v);

    /**
     * Return a vector of this vector's components, each clamped to the
     * respective minimum and maximum components.
     */
    Vec3 clamped(Vec3 min, Vec3 max);

    /**
     * Add the given coarse vector components to this vector
     */
    Vec3 plus(int x, int y, int z);

    /**
     * Add the given fine vector components to this vector
     */
    Vec3 plus(double x, double y, double z);

    /**
     * Add the given vector to this one
     */
    Vec3 plus(Vec3 v);

    /**
     * Add the given value to all components of this vector
     */
    default Vec3 plus(int xyz) {
        return plus(xyz, xyz, xyz);
    }

    /**
     * Add the given value to all components of this vector
     */
    default Vec3 plus(double xyz) {
        return plus(xyz, xyz, xyz);
    }

    default Vec3 plus(Axis axis, int delta) {
        return plus(axis.positive().times(delta));
    }

    default Vec3 plus(Axis axis, double delta) {
        return plus(axis.positive().times(delta));
    }

    /**
     * Subtract the given coarse vector components from this one
     */
    Vec3 minus(int x, int y, int z);

    /**
     * Subtract the given fine vector components from this one
     */
    Vec3 minus(double x, double y, double z);

    /**
     * Subtract the given vector from this one
     */
    Vec3 minus(Vec3 v);

    /**
     * Subtract the given value from all components of this vector
     */
    default Vec3 minus(int xyz) {
        return minus(xyz, xyz, xyz);
    }

    /**
     * Subtract the given value from all components of this vector
     */
    default Vec3 minus(double xyz) {
        return minus(xyz, xyz, xyz);
    }

    default Vec3 minus(Axis axis, int delta) {
        return minus(axis.positive().times(delta));
    }

    default Vec3 minus(Axis axis, double delta) {
        return minus(axis.positive().times(delta));
    }

    default Vec3 west(int delta) {
        return minus(Axis.X, delta);
    }

    default Vec3 east(int delta) {
        return plus(Axis.X, delta);
    }

    default Vec3 down(int delta) {
        return minus(Axis.Y, delta);
    }

    default Vec3 up(int delta) {
        return plus(Axis.Y, delta);
    }

    default Vec3 north(int delta) {
        return minus(Axis.Z, delta);
    }

    default Vec3 south(int delta) {
        return plus(Axis.Z, delta);
    }

    default Vec3 west()  { return west(1); }
    default Vec3 east()  { return east(1); }
    default Vec3 down()  { return down(1); }
    default Vec3 up()    { return up(1); }
    default Vec3 north() { return north(1); }
    default Vec3 south() { return south(1); }

    /**
     * Multiply the components of this vector by the respective given values
     */
    Vec3 times(int x, int y, int z);

    /**
     * Multiply the components of this vector by the respective given values
     */
    Vec3 times(double x, double y, double z);

    /**
     * Multiply the components of this vector by the respective components of the given vector
     */
    Vec3 times(Vec3 v);

    /**
     * Multiply this vector by the given scalar
     */
    default Vec3 times(int n) {
        return times(n, n, n);
    }

    /**
     * Multiply this vector by the given scalar
     */
    default Vec3 times(double n) {
        return times(n, n, n);
    }

    Vec3 over(int x, int y, int z);

    Vec3 over(double x, double y, double z);

    Vec3 over(Vec3 v);

    default Vec3 over(int n) {
        return over(n, n, n);
    }

    default Vec3 over(double n) {
        return over(n, n, n);
    }

    /**
     * Negate this vector
     */
    Vec3 negate();

    /**
     * Return a fine vector of length 1 pointing in the same direction as this vector
     */
    default Vec3 unit() {
        final double n = length();
        return fineOf(fineX() / n, fineY() / n, fineZ() / n);
    }

    /**
     * Return the dot product of this vector and the given vector
     */
    default double dot(Vec3 v) {
        return fineX() * v.fineX() +
               fineY() * v.fineY() +
               fineZ() * v.fineZ();
    }

    default double lengthSquared() {
        return dot(this);
    }

    default double length() {
        return Math.sqrt(lengthSquared());
    }

    default double distanceSquared(Vec3 v) {
        return v.minus(this).lengthSquared();
    }

    default double distance(Vec3 v) {
        return Math.sqrt(distanceSquared(v));
    }

    /**
     * Return a fine vector interpolating linearly between this vector and the given vector
     */
    default Vec3 interpolate(Vec3 v, double n) {
        final double u = 1 - n;
        return fineOf(u * fineX() + n * v.fineX(),
                      u * fineY() + n * v.fineY(),
                      u * fineZ() + n * v.fineZ());
    }

    /**
     * Return a fine vector half way between this vector and the given vector
     */
    default Vec3 midway(Vec3 v) {
        return fineOf((fineX() + v.fineX()) / 2D,
                      (fineY() + v.fineY()) / 2D,
                      (fineZ() + v.fineZ()) / 2D);
    }

    default Direction direction() {
        return Direction.fromVector(fineX(), fineY(), fineZ());
    }
}
