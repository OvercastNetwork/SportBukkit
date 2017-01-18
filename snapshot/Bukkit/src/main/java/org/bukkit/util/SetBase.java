package org.bukkit.util;

import java.util.Collection;
import java.util.Set;

public interface SetBase<E> extends Set<E> {

    @Override
    default boolean isEmpty() {
        return size() <= 0;
    }

    @Override
    default boolean containsAll(Collection<?> c) {
        for(Object o : c) {
            if(contains(o)) return true;
        }
        return false;
    }

    @Override
    default Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean add(E vec3) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void clear() {
        throw new UnsupportedOperationException();
    }
}
