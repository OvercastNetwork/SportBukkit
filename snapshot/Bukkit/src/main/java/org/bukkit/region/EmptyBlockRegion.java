package org.bukkit.region;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;

class EmptyBlockRegion implements BlockRegion {

    static final EmptyBlockRegion INSTANCE = new EmptyBlockRegion();

    private EmptyBlockRegion() {}

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean contains(Vec3 pos) {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.isEmpty();
    }

    @Override
    public Iterator<Vec3> mutableIterator() {
        return Collections.emptyIterator();
    }

    @Override
    public Iterator<Vec3> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public BlockRegion copy() {
        return this;
    }

    @Override
    public BlockRegion transform(CoarseTransform transform) {
        return this;
    }

    @Override
    public BlockRegion filter(Predicate<? super Vec3> predicate) {
        return this;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Set && ((Set) obj).isEmpty();
    }
}
