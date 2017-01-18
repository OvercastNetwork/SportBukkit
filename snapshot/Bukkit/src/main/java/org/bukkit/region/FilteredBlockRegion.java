package org.bukkit.region;

import java.util.Iterator;
import java.util.function.Predicate;

import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;

import static com.google.common.base.Preconditions.checkNotNull;

class FilteredBlockRegion implements BlockRegion {

    protected final BlockRegion unfiltered;
    protected final Predicate<? super Vec3> filter;

    FilteredBlockRegion(BlockRegion unfiltered, Predicate<? super Vec3> filter) {
        this.unfiltered = checkNotNull(unfiltered);
        this.filter = checkNotNull(filter);
    }

    @Override
    public boolean isMutable() {
        return true; // Cannot be sure the predicate is constant
    }

    @Override
    public boolean isEmpty() {
        for(Vec3 p : unfiltered) {
            if(filter.test(p)) return false;
        }
        return true;
    }

    @Override
    public int size() {
        int count = 0;
        for(Vec3 p : unfiltered) {
            if(filter.test(p)) count++;
        }
        return count;
    }

    @Override
    public boolean contains(Vec3 position) {
        return unfiltered.contains(position) && filter.test(position);
    }

    @Override
    public Iterator<Vec3> mutableIterator() {
        return FilteredBlockIterator.of(unfiltered.mutableIterator(), filter);
    }

    @Override
    public int hashCode() {
        return standardHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return standardEquals(obj);
    }

    @Override
    public BlockRegion copy() {
        return BakedBlockRegion.of(this);
    }

    @Override
    public BlockRegion transform(CoarseTransform transform) {
        if(transform.isIdentity()) return this;
        return new TransformedBlockRegion(this, transform);
    }

    @Override
    public BlockRegion filter(Predicate<? super Vec3> predicate) {
        if(filter.equals(predicate)) return this;
        return new FilteredBlockRegion(unfiltered, filter.and((Predicate) predicate));
    }
}
