package org.bukkit.geometry;

import java.util.Random;

import org.bukkit.region.BlockRegion;

public interface Region {

    /**
     * Does this cuboid contain no points?
     */
    boolean isEmpty();

    /**
     * Does this region contain the center point of any blocks?
     */
    boolean isBlockEmpty();

    /**
     * Does this region have a finite volume?
     */
    boolean isFinite();

    /**
     * Does this region contain a finite number of blocks?
     */
    boolean isBlockFinite();

    /**
     * Return the volume of this region
     */
    double volume();

    /**
     * Return the number of blocks with center points inside this region.
     */
    int blockVolume() throws ArithmeticException;

    /**
     * Does this region contain the given point?
     */
    boolean contains(Vec3 point);

    /**
     * Does this region contain the center point of the given block?
     */
    boolean containsBlock(Vec3 v);

    /**
     * Generate an evenly distributed random point inside this region,
     * using the given source of random numbers. If the region is empty,
     * the NaN vector is returned.
     *
     * @throws ArithmeticException if this region is unbounded
     */
    Vec3 randomPointInside(Random random) throws ArithmeticException;

    /**
     * Return a randomly chosen block with a center point inside this region.
     *
     * @throws ArithmeticException if this region is block-empty or block-unbounded
     */
    Vec3 randomBlockInside(Random random) throws ArithmeticException;

    /**
     * Return a {@link Cuboid} that contains this entire region.
     *
     * Implementations should try to make the cuboid small,
     * but it does not have to be minimal.
     */
    Cuboid bounds();

    /**
     * Return a {@link BlockRegion} of all the blocks with center points
     * inside this region.
     */
    BlockRegion blockRegion() throws ArithmeticException;

    Region transform(Transform transform);

    /**
     * @throws ArithmeticException if this region is unbounded
     */
    default void assertFinite() throws ArithmeticException {
        if(!isFinite()) {
            throw new ArithmeticException("Region is unbounded");
        }
    }

    /**
     * @throws ArithmeticException unless this region contains a finite number of blocks
     */
    default void assertBlockFinite() throws ArithmeticException {
        if(!isBlockFinite()) {
            throw new ArithmeticException("Region is not block-bounded");
        }
    }

    /**
     * @throws ArithmeticException if this region is empty
     */
    default void assertNonEmpty() throws ArithmeticException {
        if(!isEmpty()) {
            throw new ArithmeticException("Region is empty");
        }
    }

    /**
     * @throws ArithmeticException if this region contains no blocks
     */
    default void assertBlockNonEmpty() throws ArithmeticException {
        if(!isEmpty()) {
            throw new ArithmeticException("Region contains no blocks");
        }
    }

    static Region everywhere() {
        return Everywhere.INSTANCE;
    }

    static Region nowhere() {
        return Nowhere.INSTANCE;
    }

    static Region empty() {
        return Nowhere.INSTANCE;
    }
}
