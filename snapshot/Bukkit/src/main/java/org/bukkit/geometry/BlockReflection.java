package org.bukkit.geometry;

/**
 * A reflection transform that can be applied to blocks,
 * i.e. reflection in either the X or Z axes, or no reflection at all.
 */
public enum BlockReflection {

    NONE(IdentityTransform.INSTANCE),

    LEFT_RIGHT(new BlockTransform(
        1, 0,
        0, -1,
        0, 0, 0
    )),

    FRONT_BACK(new BlockTransform(
        -1, 0,
        0, 1,
        0, 0, 0
    ));

    public static final BlockReflection X = FRONT_BACK;
    public static final BlockReflection Z = LEFT_RIGHT;

    public static final BlockReflection EAST_WEST = FRONT_BACK;
    public static final BlockReflection NORTH_SOUTH = LEFT_RIGHT;

    /**
     * Return the reflection along the given {@link Axis}, which must be X or Z.
     */
    public static BlockReflection inAxis(Axis axis) {
        switch(axis) {
            case X: return FRONT_BACK;
            case Z: return LEFT_RIGHT;
        }
        throw new IllegalArgumentException("Cannot reflect blocks in the " + axis + " axis");
    }

    private final CoarseTransform transform;

    BlockReflection(CoarseTransform transform) {
        this.transform = transform;
    }

    public boolean isIdentity() {
        return this == NONE;
    }

    /**
     * Return a {@link CoarseTransform} equivalent to this reflection.
     */
    public CoarseTransform transform() {
        return transform;
    }
}
