package org.bukkit.region;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;

class BakedBlockRegion implements BlockRegion {

    private final ImmutableSet<Vec3> positions;

    BakedBlockRegion(ImmutableSet<Vec3> positions) {
        this.positions = positions;
    }

    static BlockRegion of(Stream<? extends Vec3> positions) {
        final ImmutableSet.Builder<Vec3> builder = ImmutableSet.builder();
        positions.forEach(builder::add);
        return of(builder.build());
    }

    static BlockRegion of(Iterator<? extends Vec3> positions) {
        final ImmutableSet<Vec3> set = ImmutableSet.copyOf(positions);
        return new BakedBlockRegion(set);
    }

    static BlockRegion of(Iterable<? extends Vec3> positions) {
        if(positions instanceof BlockRegion) {
            final BlockRegion region = (BlockRegion) positions;
            if(!region.isMutable()) return region;
        }
        return of(ImmutableSet.copyOf(positions));
    }

    static BlockRegion of(Collection<? extends Vec3> positions) {
        if(positions.isEmpty()) return EmptyBlockRegion.INSTANCE;
        if(positions instanceof BlockRegion) {
            final BlockRegion region = (BlockRegion) positions;
            if(!region.isMutable()) return region;
        }
        return new BakedBlockRegion(ImmutableSet.copyOf(positions));
    }

    static BlockRegion of(ImmutableSet<? extends Vec3> positions) {
        if(positions.isEmpty()) return EmptyBlockRegion.INSTANCE;
        return new BakedBlockRegion((ImmutableSet<Vec3>) positions);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public BlockRegion copy() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return positions.isEmpty();
    }

    @Override
    public int size() {
        return positions.size();
    }

    @Override
    public boolean contains(Vec3 position) {
        return positions.contains(position);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return positions.containsAll(c);
    }

    @Override
    public Iterator<Vec3> mutableIterator() {
        return positions.iterator();
    }

    @Override
    public Iterator<Vec3> iterator() {
        return positions.iterator();
    }

    @Override
    public BlockRegion transform(CoarseTransform transform) {
        if(transform.isIdentity()) return this;
        return new TransformedBlockRegion(this, transform);
    }

    @Override
    public BlockRegion filter(Predicate<? super Vec3> predicate) {
        return new FilteredBlockRegion(this, predicate);
    }

    @Override
    public int hashCode() {
        return positions.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return positions.equals(obj);
    }
}
