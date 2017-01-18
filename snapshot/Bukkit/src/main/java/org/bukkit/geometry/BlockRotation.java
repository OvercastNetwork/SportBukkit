package org.bukkit.geometry;

import org.bukkit.util.NumberConversions;

/**
 * A rotation transform that can be applied to blocks,
 * i.e. a rotation around the Y axis in units of 90 degrees.
 *
 * Wherever rotations are represented as a number of turns,
 * these turns are always clockwise looking down.
 */
public enum BlockRotation {

    NONE(IdentityTransform.INSTANCE),
    CLOCKWISE_90(makeTransform(1)),
    CLOCKWISE_180(makeTransform(2)),
    COUNTERCLOCKWISE_90(makeTransform(3));

    public static final BlockRotation CLOCKWISE_270 = COUNTERCLOCKWISE_90;
    public static final BlockRotation COUNTERCLOCKWISE_180 = CLOCKWISE_180;
    public static final BlockRotation COUNTERCLOCKWISE_270 = CLOCKWISE_90;

    private static int sin(int turns) {
        switch(turns) {
            default: return 0;
            case 1: return 1;
            case 3: return -1;
        }
    }

    private static int cos(int turns) {
        switch(turns) {
            default: return 0;
            case 0: return 1;
            case 2: return -1;
        }
    }

    private static CoarseTransform makeTransform(int turns) {
        return new BlockTransform(
            cos(turns), -sin(turns),
            sin(turns), cos(turns),
            0, 0, 0
        );
    }

    /**
     * Return the rotation for the given number of 90 degree clockwise turns.
     *
     * The number can be any positive or negative amount.
     */
    public static BlockRotation turns(int turns) {
        return values()[NumberConversions.mod(turns, 4)];
    }

    private final CoarseTransform transform;

    BlockRotation(CoarseTransform transform) {
        this.transform = transform;
    }

    public boolean isIdentity() {
        return this == NONE;
    }

    /**
     * Return a {@link CoarseTransform} equivalent to this rotation.
     */
    public CoarseTransform transform() {
        return transform;
    }

    /**
     * Return the number of 90-degree clockwise turns for this rotation.
     *
     * This is always in the range 0 to 3 inclusive.
     */
    public int turns() {
        return ordinal();
    }

    /**
     * Apply this rotation to the given {@link Axis}
     */
    public Axis apply(Axis axis) {
        if(turns() % 2 == 1) {
            switch(axis) {
                case X: return Axis.Z;
                case Z: return Axis.X;
            }
        }
        return axis;
    }
}
