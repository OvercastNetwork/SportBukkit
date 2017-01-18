package org.bukkit.geometry;

/**
 * A transform that can be applied to blocks, i.e. any combination
 * of reflection along horizontal axes, right-angle rotation, and
 * translation by integer offsets.
 */
public interface CoarseTransform extends Transform {

    BlockRotoflection orientation();

    @Override
    CoarseTransform inverse();

    CoarseTransform compose(CoarseTransform before);

    CoarseTransform andThen(CoarseTransform after);

    @Override
    default CoarseTransform translate(int x, int y, int z) {
        return andThen(translation(x, y, z));
    }

    @Override
    default CoarseTransform translate(Vec3 offset) {
        return andThen(translation(offset));
    }

    @Override
    default CoarseTransform reflect(Axis axis) {
        return andThen(reflection(axis));
    }

    @Override
    default CoarseTransform rotate(int turns) {
        return andThen(rotation(turns));
    }

    static CoarseTransform translation(int x, int y, int z) {
        if(x == 0 && y == 0 && z == 0) return Transform.identity();
        return new BlockTransform(
            1, 0,
            0, 1,
            x, y, z
        );
    }

    static CoarseTransform translation(Vec3 offset) {
        return translation(offset.coarseX(), offset.coarseY(), offset.coarseZ());
    }

    static CoarseTransform reflection(Axis axis) {
        return BlockReflection.inAxis(axis).transform();
    }

    static CoarseTransform rotation(int turns) {
        return BlockRotation.turns(turns).transform();
    }
}
