package org.bukkit.region;

import java.util.Iterator;
import java.util.function.Predicate;

import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;

class TransformedBlockRegion implements BlockRegion {

    private final BlockRegion original;
    private final CoarseTransform transform, inverse;

    TransformedBlockRegion(BlockRegion original, CoarseTransform transform) {
        this.original = original;
        this.transform = transform;
        this.inverse = transform.inverse();
    }

    @Override
    public boolean isMutable() {
        return original.isMutable();
    }

    @Override
    public boolean isEmpty() {
        return original.isEmpty();
    }

    @Override
    public int size() {
        return original.size();
    }

    @Override
    public boolean contains(Vec3 pos) {
        return original.contains(inverse.apply(pos));
    }

    @Override
    public Iterator<Vec3> mutableIterator() {
        return TransformedBlockIterator.of(original.mutableIterator(), transform);
    }

    @Override
    public BlockRegion copy() {
        if(!original.isMutable()) return this;
        return BakedBlockRegion.of(this);
    }

    @Override
    public BlockRegion transform(CoarseTransform transform) {
        if(transform.isIdentity()) return this;
        transform = this.transform.andThen(transform);
        if(transform.isIdentity()) return original;
        return new TransformedBlockRegion(original, transform);
    }

    @Override
    public BlockRegion filter(Predicate<? super Vec3> predicate) {
        return new FilteredBlockRegion(this, predicate);
    }

    @Override
    public int hashCode() {
        return standardHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return standardEquals(obj);
    }
}
