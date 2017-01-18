package org.bukkit.geometry;

import org.bukkit.block.BlockFace;

class IdentityTransform implements CoarseTransform {

    static final IdentityTransform INSTANCE = new IdentityTransform();

    private IdentityTransform() {}

    @Override
    public boolean isIdentity() {
        return true;
    }

    @Override
    public CoarseTransform inverse() {
        return this;
    }

    @Override
    public Vec3 apply(Vec3 v) {
        return v.copy();
    }

    @Override
    public void applyInPlace(MutableVec3 v) {
    }

    @Override
    public BlockFace apply(BlockFace face) {
        return face;
    }

    @Override
    public CoarseTransform compose(CoarseTransform before) {
        return before;
    }

    @Override
    public CoarseTransform andThen(CoarseTransform after) {
        return after;
    }

    @Override
    public Transform compose(Transform before) {
        return before;
    }

    @Override
    public Transform andThen(Transform after) {
        return after;
    }

    @Override
    public BlockRotoflection orientation() {
        return BlockRotoflection.identity();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Transform &&
               ((Transform) that).isIdentity();
    }

    @Override
    public String toString() {
        return Transform.class.getSimpleName() + "{identity}";
    }
}
