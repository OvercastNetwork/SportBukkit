package org.bukkit.region;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.bukkit.geometry.MutableVec3;
import org.bukkit.geometry.Vec3;

public class FilteredBlockIterator extends MutableBlockIterator {

    public static Iterator<Vec3> of(Iterator<Vec3> iterator, Predicate<? super Vec3> filter) {
        if(iterator instanceof FilteredBlockIterator) {
            final FilteredBlockIterator filtered = (FilteredBlockIterator) iterator;
            return new FilteredBlockIterator(filtered.iterator, v -> filtered.filter.test(v) && filter.test(v));
        }
        return new FilteredBlockIterator(iterator, filter);
    }

    private final Iterator<Vec3> iterator;
    private final Predicate<? super Vec3> filter;

    private Vec3 next = null;

    private FilteredBlockIterator(Iterator<Vec3> iterator, Predicate<? super Vec3> filter) {
        this.iterator = iterator;
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {
        if(next != null) return true;

        while(iterator.hasNext()) {
            Vec3 v = iterator.next();

            if(filter.test(v)) {
                next = v;
                return true;
            }
        }

        return false;
    }

    @Override
    protected void advance(MutableVec3 value) {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }

        value.set(next);
        next = null;
    }
}
