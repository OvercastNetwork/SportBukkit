package org.bukkit.region;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Vec3;

class BlockRegionAdapter implements BlockRegion {

    private final Set<Vec3> positions;

    BlockRegionAdapter(Set<Vec3> positions) {
        this.positions = positions;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public BlockRegion copy() {
        return BakedBlockRegion.of(positions);
    }

    @Override
    public BlockRegion transform(CoarseTransform transform) {
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

    @Override
    public boolean contains(Vec3 pos) {
        return positions.contains(pos);
    }

    @Override
    public Iterator<Vec3> mutableIterator() {
        return positions.iterator();
    }

    @Override
    public int size() {
        return positions.size();
    }

    @Override
    public boolean isEmpty() {
        return positions.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return positions.contains(o);
    }

    @Override
    public Iterator<Vec3> iterator() {
        return positions.iterator();
    }

    @Override
    public Object[] toArray() {
        return positions.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return positions.toArray(a);
    }

    @Override
    public boolean add(Vec3 vec3) {
        return positions.add(vec3);
    }

    @Override
    public boolean remove(Object o) {
        return positions.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return positions.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Vec3> c) {
        return positions.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return positions.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return positions.removeAll(c);
    }

    @Override
    public void clear() {
        positions.clear();
    }

    @Override
    public Spliterator<Vec3> spliterator() {
        return positions.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super Vec3> filter) {
        return positions.removeIf(filter);
    }

    @Override
    public Stream<Vec3> stream() {
        return positions.stream();
    }

    @Override
    public Stream<Vec3> parallelStream() {
        return positions.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super Vec3> action) {
        positions.forEach(action);
    }
}
