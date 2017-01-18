package org.bukkit.region;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterators;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;
import org.bukkit.util.SetBase;

/**
 * Represents a set of block coordinates, and supports iteration over them and tests for inclusion.
 *
 * Some region types have changing contents. This can be checked with {@link #isMutable()}, and an
 * immutable region can always be obtained from {@link #copy()}. Other operations may return mutable
 * regions, even when applied to immutable regions.
 *
 * Since regions are {@link Set}s, they must follow the equality contract for sets, and compare equal
 * to any other {@link Set} of the same {@link Vec3} elements.
 */
public interface BlockRegion extends SetBase<Vec3> {

    /**
     * Can the contents of this region change?
     *
     * If this returns false, then the set of blocks in the region must NEVER change.
     * If this returns true, then the set of blocks may or may not change.
     */
    boolean isMutable();

    boolean contains(Vec3 pos);

    @Override
    default boolean contains(Object o) {
        return o instanceof Vec3 && contains((Vec3) o);
    }

    default boolean containsAll(BlockRegion region) {
        // Use the mutable iterator for efficiency
        for(Vec3 pos : region.mutableIterable()) {
            if(!contains(pos)) return false;
        }
        return true;
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        return c instanceof BlockRegion ? containsAll((BlockRegion) c)
                                        : SetBase.super.containsAll(c);
    }

    /**
     * Return an {@link Iterator} over the blocks in this region that MAY return the same {@link Vec3}
     * instance from multiple calls to {@link Iterator#next()}, with the vector assuming a new value
     * for each iteration.
     *
     * This can be considerably more efficient, but care must be taken to not use vector instances
     * outside of their own iteration, without copying them first. Call {@link #iterator()} for an
     * iterator that returns an independent, immutable vector for each iteration.
     */
    Iterator<Vec3> mutableIterator();

    default Iterable<Vec3> mutableIterable() {
        return this::mutableIterator;
    }

    default Iterator<Vec3> iterator() {
        return Iterators.transform(mutableIterator(), Vec3::copy);
    }

    /**
     * Return an immutable {@link BlockRegion} containing the same positions that this
     * region currently contains. If this region is immutable, it may return itself.
     */
    BlockRegion copy();

    /**
     * Return a region equal to this region with the given transform applied to all elements.
     *
     * The returned region is a live view of this one, and any changes to this region are
     * immediately reflected in the transformed region. If this region is immutable, then
     * the transformed region will also be immutable.
     *
     * This region may return itself if it would be unaffected by the transform.
     */
    BlockRegion transform(CoarseTransform transform);

    /**
     * Return a region containing the elements of this region that pass the given predicate.
     *
     * The returned region is a live view of this one, and any changes to this region are
     * immediately reflected in the filtered region.
     *
     * This region may return itself if it would be unaffected by the filter.
     */
    BlockRegion filter(Predicate<? super Vec3> predicate);

    default int standardHashCode() {
        int h = 0;
        for(Vec3 p : mutableIterable()) {
            h += p.hashCode();
        }
        return h;
    }

    default boolean standardEquals(Object that) {
        return that instanceof Set &&
               size() == ((Set) that).size() &&
               containsAll((Set) that);
    }

    /**
     * Return the empty region
     */
    static BlockRegion empty() {
        return EmptyBlockRegion.INSTANCE;
    }

    /**
     * Return a live view of the given {@link Set} as a {@link BlockRegion}.
     *
     * Changes to the underlying set are instantly reflected in the region.
     */
    static BlockRegion of(Set<Vec3> positions) {
        if(positions instanceof BlockRegion) {
            return (BlockRegion) positions;
        }
        if(positions instanceof ImmutableCollection) {
            return BakedBlockRegion.of(positions);
        }
        return new BlockRegionAdapter(positions);
    }

    /**
     * Return an immutable region containing exactly the given set of block positions.
     */
    static BlockRegion copyOf(Stream<Vec3> positions) {
        return BakedBlockRegion.of(positions);
    }

    /**
     * Return an immutable region containing exactly the given set of block positions.
     */
    static BlockRegion copyOf(Iterator<Vec3> positions) {
        return BakedBlockRegion.of(positions);
    }

    /**
     * Return an immutable region containing exactly the given set of block positions.
     */
    static BlockRegion copyOf(Iterable<Vec3> positions) {
        return BakedBlockRegion.of(positions);
    }
}
