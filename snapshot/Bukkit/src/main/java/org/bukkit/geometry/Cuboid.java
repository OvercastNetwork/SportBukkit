package org.bukkit.geometry;

import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.region.BlockRegion;
import org.bukkit.region.CuboidBlockRegion;
import org.bukkit.util.ImVector;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Represents an axis-aligned cuboid (box) in fine (real-valued) coordinates
 *
 * In fine-space, all boundaries of the cuboid are closed. That is, points on
 * any boundary are considered inside the cuboid. Blocks are considered inside
 * the cuboid if and only if their center points are inside.
 *
 * Cuboids can be unbounded in any combination of the 6 directions, in which case
 * the respective coordinate will be +/- Infinity.
 *
 * Cuboids can have zero length on any combination of axes, in which case the
 * minimum and maximum coordinates on those axes will be equal. If any axis has
 * zero length, then the cuboid has zero volume, but may still contain points
 * that lie on the degenerate boundary.
 *
 * There is a single "empty cuboid" which has the following properties:
 *
 *      - It is equal only to itself.
 *      - It contains no points or other spatial objects, except for itself.
 *      - It is contained by all other cuboids.
 *      - It's minimum and maximum points are the NaN vector, as are any other points
 *        in space that are generated from the cuboid.
 *      - Attempts to generate any coarse points will throw an {@link ArithmeticException}.
 *      - Any relative measurements of the cuboid (such as size or volume) are zero.
 *
 * The empty cuboid is returned in these cases:
 *
 *      - Calling {@link #empty()}
 *      - Trying to construct a cuboid with any NaN values
 *      - Trying to construct a cuboid with negative length in any axis
 *      - Trying to construct a cuboid enclosing an empty set of points
 */
public abstract class Cuboid implements Region {

    protected final ImVector min;
    protected final ImVector max;

    Cuboid(Vec3 min, Vec3 max) {
        this.min = ImVector.copyOf(min);
        this.max = ImVector.copyOf(max);
    }

    /**
     * Create a minimal cuboid enclosing the given pair of points
     *
     * The relative order of the points on any axis does not matter.
     */
    public static Cuboid between(Vec3 a, Vec3 b) {
        if(a.anyNaN() || b.anyNaN()) return empty();
        return new NonEmptyCuboid(a.minimum(b), a.maximum(b));
    }

    /**
     * Create a cuboid with the given minimum corner and size
     */
    public static Cuboid fromMinAndSize(Vec3 min, Vec3 size) {
        if(min.anyNaN() || size.anyNaN() ||
           size.fineX() < 0 || size.fineY() < 0 || size.fineZ() < 0) return empty();
        return new NonEmptyCuboid(min, min.plus(size));
    }

    /**
     * Create a minimal bounding box containing all of the given points
     */
    public static Cuboid enclosing(Vec3... points) {
        if(points.length == 0) return empty();

        final Vector min = new Vector(Double.POSITIVE_INFINITY);
        final Vector max = new Vector(Double.NEGATIVE_INFINITY);

        for(Vec3 p : points) {
            if(p.anyNaN()) return empty();
            min.minimize(p);
            max.maximize(p);
        }

        return between(min, max);
    }

    /**
     * Return the cuboid that is unbounded in all directions
     */
    public static Cuboid unbounded() {
        return NonEmptyCuboid.UNBOUNDED;
    }

    /**
     * Return the inverse of the {@link #unbounded()} cuboid
     */
    public static Cuboid empty() {
        return EmptyCuboid.INSTANCE;
    }

    /**
     * Return the largest cuboid contained entirely within both of the given cuboids
     */
    public static Cuboid intersect(Cuboid a, Cuboid b) {
        if(a.contains(b)) {
            return b;
        } else if(b.contains(a)) {
            return a;
        } else {
            return between(a.min.maximum(b.min),
                           a.max.minimum(b.max));
        }
    }

    /**
     * Return the smallest cuboid containing both of the given cuboids
     */
    public static Cuboid union(Cuboid a, Cuboid b) {
        if(a.contains(b)) {
            return a;
        } else if(b.contains(a)) {
            return b;
        } else {
            return between(a.min.minimum(b.min),
                           a.max.maximum(b.max));
        }
    }

    /**
     * Return the smallest cuboid containing the given original cuboid, after subtracting the other given cuboid.
     */
    public static Cuboid complement(Cuboid original, Cuboid subtracted) {
        // The booleans reflect if the subtracted set contains the
        // original set on each axis. The final bounds for each axis
        // are then the minimum of the two sets, if the other two axes
        // are containing, otherwise the bounds of the original set.
        boolean cx = subtracted.min.fineX() < original.min.fineX() && subtracted.max.fineX() > original.max.fineX();
        boolean cy = subtracted.min.fineY() < original.min.fineY() && subtracted.max.fineY() > original.max.fineY();
        boolean cz = subtracted.min.fineZ() < original.min.fineZ() && subtracted.max.fineZ() > original.max.fineZ();
        return between(ImVector.of(cy && cz ? Math.max(original.min.fineX(), subtracted.min.fineX()) : original.min.fineX(),
                                   cz && cx ? Math.max(original.min.fineY(), subtracted.min.fineY()) : original.min.fineY(),
                                   cx && cy ? Math.max(original.min.fineZ(), subtracted.min.fineZ()) : original.min.fineZ()),
                       ImVector.of(cy && cz ? Math.min(original.max.fineX(), subtracted.max.fineX()) : original.max.fineX(),
                                   cz && cx ? Math.min(original.max.fineY(), subtracted.max.fineY()) : original.max.fineY(),
                                   cx && cy ? Math.min(original.max.fineZ(), subtracted.max.fineZ()) : original.max.fineZ()));
    }

    @Override
    public Cuboid bounds() {
        return this;
    }

    /**
     * Return the distance from the origin of the given ray to the point
     * where it first intersects this cuboid. If the ray originates inside
     * the cuboid, zero is returned.
     *
     * If the ray does not intersect the cuboid, but the line of the ray does
     * intersect (behind the ray's origin), then a negative distance is returned.
     *
     * In any other case, NaN is returned.
     */
    public abstract double intersectionDistance(Ray ray);

    /**
     * Return the point at which the given ray enters this cuboid,
     * or null if the ray does not intersect the cuboid. If the ray
     * originates inside the cuboid, the ray's origin point is returned.
     */
    public abstract ImVector intersect(Ray ray);


    /**
     * Construct a cuboid by translating this cuboid by the given offset
     */
    public abstract Cuboid translate(Vec3 offset);

    public abstract Cuboid transform(Transform transform);

    /**
     * Does this cuboid fully enclose the given cuboid?
     */
    public abstract boolean contains(Cuboid cuboid);

    /**
     * Return the minimum corner of this cuboid
     */
    public ImVector minimum() {
        return min;
    }

    /**
     * Return the maximum corner of this cuboid
     */
    public ImVector maximum() {
        return max;
    }

    /**
     * Return the dimensions of this cuboid
     */
    public ImVector size() {
        return max.minus(min);
    }

    public double volume() {
        final ImVector size = size();
        return size.fineX() * size.fineY() * size.fineZ();
    }

    /**
     * Return the center point of this cuboid
     */
    public ImVector center() {
        return ImVector.copyOf(min.midway(max));
    }

    /**
     * Return the eight corner points of this cuboid
     */
    public ImVector[] vertices() {
        return new ImVector[] {
            min,
            ImVector.of(min.fineX(), min.fineY(), max.fineZ()),
            ImVector.of(min.fineX(), max.fineY(), min.fineZ()),
            ImVector.of(min.fineX(), max.fineY(), max.fineZ()),
            ImVector.of(max.fineX(), min.fineY(), min.fineZ()),
            ImVector.of(max.fineX(), min.fineY(), max.fineZ()),
            ImVector.of(max.fineX(), max.fineY(), min.fineZ()),
            max
        };
    }

    /**
     * Generate an evenly distributed random point inside this cuboid,
     * using the given source of random numbers.
     *
     * {@link Random#nextDouble()} is used to choose the individual
     * components, so the range of possible coordinates includes the
     * lower bounds of the cuboid, but not the upper bounds.
     *
     * @throws ArithmeticException if the cuboid is unbounded
     */
    public abstract ImVector randomPointInside(Random random);

    /**
     * Return the lowest block with a center point inside this cuboid
     */
    public abstract Vec3 minimumBlockInside();

    /**
     * Return the highest block with a center point inside this cuboid
     */
    public abstract Vec3 maximumBlockInside();

    /**
     * Return the lowest block with a center point that is greater,
     * on all axes, than any point in the cuboid.
     *
     * This block will have coordinates one greater than {@link #maximumBlockInside()}.
     */
    public abstract Vec3 minimumBlockOutside();

    /**
     * Return number of blocks on each axis with center points inside this cuboid.
     */
    public abstract Vec3 blockSize();
}

class NonEmptyCuboid extends Cuboid {

    static final Cuboid UNBOUNDED = new NonEmptyCuboid(ImVector.of(Double.NEGATIVE_INFINITY),
                                                       ImVector.of(Double.POSITIVE_INFINITY));

    public NonEmptyCuboid(Vec3 min, Vec3 max) {
        super(min, max);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{min=[" + min.toString() + "],max=[" + max.toString() + "]}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Cuboid)) return false;
        final Cuboid that = (Cuboid) obj;
        return min.equals(that.minimum()) && max.equals(that.max);
    }

    /**
     * Return the distance from the origin of the given ray to the point
     * where it first intersects this cuboid. If the ray originates inside
     * the cuboid, zero is returned.
     *
     * If the ray does not intersect the cuboid, but the line of the ray does
     * intersect (behind the ray's origin), then a negative distance is returned.
     *
     * In any other case, NaN is returned.
     */
    public double intersectionDistance(Ray ray) {
        // Find the distances to both boundaries on each axis
        final ImVector d1 = min.minus(ray.origin()).over(ray.normal());
        final ImVector d2 = max.minus(ray.origin()).over(ray.normal());

        // Sort the distances into near and far for each axis
        final ImVector near = d1.minimum(d2);
        final ImVector far = d1.maximum(d2);

        // Last near point enters the cuboid
        // First far point exits the cuboid
        final double enter = NumberConversions.max(near.fineX(), near.fineY(), near.fineZ());
        final double exit = NumberConversions.min(far.fineX(), far.fineY(), far.fineZ());

        // If the ray enters the cuboid before it exits, then it intersects
        return enter <= exit ? enter : Double.NaN;
    }

    /**
     * Return the point at which the given ray enters this cuboid,
     * or null if the ray does not intersect the cuboid. If the ray
     * originates inside the cuboid, the ray's origin point is returned.
     */
    public ImVector intersect(Ray ray) {
        final double distance = intersectionDistance(ray);
        if(distance == 0) {
            return ray.origin();
        } else if(distance > 0) {
            return ray.origin().plus(ray.normal().times(distance));
        } else {
            return null;
        }
    }

    /**
     * Construct a cuboid by translating this cuboid by the given offset
     */
    public Cuboid translate(Vec3 offset) {
        return offset.isZero() ? this : new NonEmptyCuboid(min.plus(offset), max.plus(offset));
    }

    public Cuboid transform(Transform transform) {
        return transform.isIdentity() ? this : new NonEmptyCuboid(transform.apply(min), transform.apply(max));
    }

    public boolean isFinite() {
        return !(Double.isInfinite(min.fineX()) || Double.isInfinite(max.fineX()) ||
                 Double.isInfinite(min.fineY()) || Double.isInfinite(max.fineY()) ||
                 Double.isInfinite(min.fineZ()) || Double.isInfinite(max.fineZ()));
    }

    public boolean isBlockFinite() {
        return !(Double.isInfinite(min.fineX()) || Double.isInfinite(max.fineX()) ||
                 Double.isInfinite(min.fineZ()) || Double.isInfinite(max.fineZ()));
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean contains(Vec3 point) {
        return min.fineLessOrEqual(point) && max.fineGreaterOrEqual(point);
    }

    /**
     * Does this cuboid fully enclose the given cuboid?
     */
    public boolean contains(Cuboid cuboid) {
        return cuboid.isEmpty() || (contains(cuboid.minimum()) && cuboid.contains(minimum()));
    }

    /**
     * Generate an evenly distributed random point inside this cuboid,
     * using the given source of random numbers.
     *
     * {@link Random#nextDouble()} is used to choose the individual
     * components, so the range of possible coordinates includes the
     * lower bounds of the cuboid, but not the upper bounds.
     *
     * @throws ArithmeticException if the cuboid is unbounded
     */
    public ImVector randomPointInside(Random random) {
        assertFinite();
        final ImVector size = size();
        return min.plus(size.fineX() * random.nextDouble(),
                        size.fineY() * random.nextDouble(),
                        size.fineZ() * random.nextDouble());
    }

    /**
     * Return the lowest block with a center point inside this cuboid
     */
    public Vec3 minimumBlockInside() {
        assertBlockFinite();
        return Bukkit.vectors().coarse(min.fineX() + 0.5d,
                                       NumberConversions.clamp(min.fineY(), 0, 256) + 0.5d,
                                       min.fineZ() + 0.5d);
    }

    /**
     * Return the highest block with a center point inside this cuboid
     */
    public Vec3 maximumBlockInside() {
        assertBlockFinite();
        return Bukkit.vectors().coarse(max.fineX() - 0.5d,
                                       NumberConversions.clamp(max.fineY(), 0, 256) - 0.5d,
                                       max.fineZ() - 0.5d);
    }

    /**
     * Return the lowest block with a center point that is greater,
     * on all axes, than any point in the cuboid.
     *
     * This block will have coordinates one greater than {@link #maximumBlockInside()}.
     */
    public Vec3 minimumBlockOutside() {
        assertBlockFinite();
        return Bukkit.vectors().coarse(max.fineX() + 0.5d,
                                       NumberConversions.clamp(max.fineY(), 0, 256) + 0.5d,
                                       max.fineZ() + 0.5d);
    }

    public boolean containsBlock(Vec3 v) {
        return v.coarseGreaterOrEqual(minimumBlockInside()) &&
               v.coarseLess(minimumBlockOutside());
    }

    /**
     * Return number of blocks on each axis with center points inside this cuboid.
     */
    public Vec3 blockSize() {
        return minimumBlockOutside().minus(minimumBlockInside());
    }

    public int blockVolume() {
        final Vec3 size = blockSize();
        if(size.coarseX() <= 0 || size.coarseY() <= 0 || size.coarseZ() <= 0) return 0;
        return size.coarseX() * size.coarseY() * size.coarseZ();
    }

    public boolean isBlockEmpty() {
        return blockVolume() <= 0;
    }

    public Vec3 randomBlockInside(Random random) {
        final Vec3 size = blockSize();
        if(size.coarseX() <= 0 || size.coarseY() <= 0 || size.coarseZ() <= 0) {
            throw new ArithmeticException("Cuboid contains no blocks");
        }

        return minimumBlockInside().plus(random.nextInt(size.coarseX()),
                                         random.nextInt(size.coarseY()),
                                         random.nextInt(size.coarseZ()));
    }

    public BlockRegion blockRegion() {
        return CuboidBlockRegion.fromMinAndSize(minimumBlockInside(), blockSize());
    }
}

class EmptyCuboid extends Cuboid implements EmptyRegion {

    static final EmptyCuboid INSTANCE = new EmptyCuboid();

    private EmptyCuboid() {
        super(ImVector.ofNaN(), ImVector.ofNaN());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Region && ((Region) obj).isEmpty();
    }

    @Override
    public double intersectionDistance(Ray ray) {
        return Double.NaN;
    }

    @Override
    public ImVector intersect(Ray ray) {
        return null;
    }

    @Override
    public Cuboid translate(Vec3 offset) {
        return this;
    }

    @Override
    public Cuboid transform(Transform transform) {
        return this;
    }

    @Override
    public boolean isFinite() {
        return true;
    }

    @Override
    public boolean isBlockFinite() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isBlockEmpty() {
        return true;
    }

    @Override
    public boolean contains(Vec3 point) {
        return false;
    }

    @Override
    public boolean contains(Cuboid cuboid) {
        return cuboid.isEmpty();
    }

    @Override
    public ImVector size() {
        return ImVector.ofZero();
    }

    @Override
    public double volume() {
        return 0;
    }

    @Override
    public ImVector randomPointInside(Random random) {
        return ImVector.ofNaN();
    }

    @Override
    public boolean containsBlock(Vec3 v) {
        return false;
    }

    @Override
    public Vec3 blockSize() {
        return ImVector.ofZero();
    }

    @Override
    public int blockVolume() {
        return 0;
    }

    @Override
    public BlockRegion blockRegion() {
        return BlockRegion.empty();
    }

    @Override
    public Vec3 minimumBlockInside() {
        throw new ArithmeticException("Region is empty");
    }

    @Override
    public Vec3 maximumBlockInside() {
        throw new ArithmeticException("Region is empty");
    }

    @Override
    public Vec3 minimumBlockOutside() {
        throw new ArithmeticException("Region is empty");
    }
}