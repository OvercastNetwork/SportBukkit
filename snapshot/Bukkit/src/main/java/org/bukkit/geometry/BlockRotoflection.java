package org.bukkit.geometry;

import org.bukkit.util.NumberConversions;

/**
 * A combined reflection and rotation transform that can be applied to blocks.
 *
 * This class can represent any of the 8 transforms that affect only the state of a block (and not its position).
 *
 * The transform is always normalized to an optional reflection in the X axis,
 * followed by a rotation of 0 to 3 clockwise right turns, around the Y axis, looking down.
 */
public class BlockRotoflection {

    private static final BlockRotoflection[] NORMAL = new BlockRotoflection[] {
        new BlockRotoflection(false, 0),
        new BlockRotoflection(false, 1),
        new BlockRotoflection(false, 2),
        new BlockRotoflection(false, 3)
    };

    private static final BlockRotoflection[] INVERTED = new BlockRotoflection[] {
        new BlockRotoflection(true, 0),
        new BlockRotoflection(true, 1),
        new BlockRotoflection(true, 2),
        new BlockRotoflection(true, 3)
    };

    public static BlockRotoflection identity() {
        return NORMAL[0];
    }

    /**
     * Return the transform equivalent to the given operations
     *
     * @param reflect    Reflect in X axis
     * @param turns      Number of clockwise right turns, around Y axis, facing down
     */
    public static BlockRotoflection of(boolean reflect, int turns) {
        turns = NumberConversions.mod(turns, 4);
        return reflect ? INVERTED[turns] : NORMAL[turns];
    }

    /**
     * Return the transform equivalent to the given operations
     */
    public static BlockRotoflection of(BlockReflection reflection, BlockRotation rotation) {
        if(reflection == BlockReflection.Z) {
            // Normalize reflection axis to X
            return of(true, rotation.turns() + 2);
        } else {
            return of(reflection == BlockReflection.X, rotation.turns());
        }
    }

    /**
     * Return the transform equivalent to the given reflection alone
     */
    public static BlockRotoflection of(BlockReflection reflection) {
        return of(reflection, BlockRotation.NONE);
    }

    /**
     * Return the transform equivalent to the given rotation alone
     */
    public static BlockRotoflection of(BlockRotation rotation) {
        return of(false, rotation.turns());
    }

    private final BlockReflection reflection;
    private final BlockRotation rotation;
    private final CoarseTransform transform;

    private BlockRotoflection(boolean reflect, int turns) {
        this.reflection = reflect ? BlockReflection.X : BlockReflection.NONE;
        this.rotation = BlockRotation.turns(turns);
        this.transform = reflection.transform().andThen(rotation.transform());
    }

    public boolean isIdentity() {
        return reflection.isIdentity() && rotation.isIdentity();
    }

    /**
     * Return the reflection component of this transform,
     * which is either {@link BlockReflection#NONE} or {@link BlockReflection#X}.
     */
    public BlockReflection reflection() {
        return reflection;
    }

    /**
     * Does this transform include a reflection?
     */
    boolean isReflected() {
        return reflection != BlockReflection.NONE;
    }

    /**
     * Return the rotation component of this transform
     */
    public BlockRotation rotation() {
        return rotation;
    }

    /**
     * Return the rotation component of this transform,
     * as a count of clockwise right turns.
     */
    int turns() {
        return rotation.turns();
    }

    /**
     * Return a {@link CoarseTransform} equivalent to this transform
     */
    public CoarseTransform transform() {
        return transform;
    }

    /**
     * Apply this transform to the given {@link Axis}
     */
    public Axis apply(Axis axis) {
        return rotation.apply(axis);
    }

    @Override
    public int hashCode() {
        return reflection.ordinal() * 4 +
               rotation.ordinal();
    }

    @Override
    public boolean equals(Object that) {
        return this == that || (
            that instanceof BlockRotoflection &&
            this.reflection.equals(((BlockRotoflection) that).reflection) &&
            this.rotation.equals(((BlockRotoflection) that).rotation)
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{reflection=" + reflection +
               " rotation=" + rotation + "}";
    }
}
