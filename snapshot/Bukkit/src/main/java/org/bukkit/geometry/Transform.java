package org.bukkit.geometry;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.bukkit.block.BlockFace;

/**
 * A transform applicable to {@link Vec3} and other spatial objects.
 *
 * Transforms are ALWAYS immutable.
 *
 * Currently, all implementations of this interface are also {@link CoarseTransform}s.
 * In the future, fine-resolution transforms, which cannot always be applied to blocks,
 * may extend this interface.
 *
 * @see CoarseTransform
 */
public interface Transform extends UnaryOperator<Vec3> {

    void applyInPlace(MutableVec3 v);

    default BlockFace apply(BlockFace face) {
        return BlockFace.byDirection(apply((Vec3) face));
    }

    boolean isIdentity();

    Transform inverse();

    Transform compose(Transform before);

    Transform andThen(Transform after);

    @Override
    default <V> Function<V, Vec3> compose(Function<? super V, ? extends Vec3> before) {
        return before instanceof Transform ? (Function<V, Vec3>) compose((Transform) before)
                                           : UnaryOperator.super.compose(before);
    }

    @Override
    default <V> Function<Vec3, V> andThen(Function<? super Vec3, ? extends V> after) {
        return after instanceof Transform ? (Function<Vec3, V>) andThen((Transform) after)
                                          : UnaryOperator.super.andThen(after);
    }

    default Transform translate(int x, int y, int z) {
        return andThen(CoarseTransform.translation(x, y, z));
    }

    default Transform translate(Vec3 offset) {
        return andThen(CoarseTransform.translation(offset));
    }

    default Transform reflect(Axis axis) {
        return andThen(CoarseTransform.reflection(axis));
    }

    default Transform rotate(int turns) {
        return andThen(CoarseTransform.rotation(turns));
    }

    static CoarseTransform identity() {
        return IdentityTransform.INSTANCE;
    }
}
