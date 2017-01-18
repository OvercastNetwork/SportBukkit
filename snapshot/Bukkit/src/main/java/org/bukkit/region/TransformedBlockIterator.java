package org.bukkit.region;

import java.util.Iterator;

import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.MutableVec3;
import org.bukkit.geometry.Vec3;

public class TransformedBlockIterator extends MutableBlockIterator {

    public static Iterator<Vec3> of(Iterator<Vec3> iterator, CoarseTransform transform) {
        if(iterator instanceof TransformedBlockIterator) {
            final TransformedBlockIterator transformed = (TransformedBlockIterator) iterator;
            return new TransformedBlockIterator(transformed.iterator, transformed.transform.andThen(transform));
        }
        return new TransformedBlockIterator(iterator, transform);
    }

    private final Iterator<Vec3> iterator;
    private final CoarseTransform transform;

    private TransformedBlockIterator(Iterator<Vec3> iterator, CoarseTransform transform) {
        this.iterator = iterator;
        this.transform = transform;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    protected void advance(MutableVec3 value) {
        value.set(iterator.next());
        transform.applyInPlace(value);
    }
}
