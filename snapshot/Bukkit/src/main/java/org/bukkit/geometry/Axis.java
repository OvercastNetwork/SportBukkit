package org.bukkit.geometry;

import org.bukkit.block.BlockFace;
import org.bukkit.util.ImVector;

/**
 * Represents one of the three spatial axes
 */
public enum Axis {

    X(ImVector.of(1, 0, 0)),
    Y(ImVector.of(0, 1, 0)),
    Z(ImVector.of(0, 0, 1));

    public static final Axis EAST_WEST = X;
    public static final Axis UP_DOWN = Y;
    public static final Axis NORTH_SOUTH = Z;

    private final Vec3 positive, negative;
    private final BlockFace positiveFace, negativeFace;

    Axis(Vec3 positive) {
        this.positive = positive;
        this.negative = positive.negate();
        this.positiveFace = BlockFace.byDirection(this.positive);
        this.negativeFace = BlockFace.byDirection(this.negative);
    }

    /**
     * Is this axis horizontally oriented?
     */
    boolean isHorizontal() {
        return this != Y;
    }

    /**
     * Return a unit vector pointing in the positive direction along this axis
     */
    public Vec3 positive() {
        return positive;
    }

    /**
     * Return a unit vector pointing in the negative direction along this axis
     */
    public Vec3 negative() {
        return negative;
    }

    /**
     * Return the {@link BlockFace} facing in the positive direction along this axis
     */
    public BlockFace positiveFace() {
        return positiveFace;
    }

    /**
     * Return the {@link BlockFace} facing in the negative direction along this axis
     */
    public BlockFace negativeFace() {
        return negativeFace;
    }
}
