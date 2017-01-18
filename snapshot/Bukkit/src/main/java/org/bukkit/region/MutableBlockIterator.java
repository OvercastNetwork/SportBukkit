package org.bukkit.region;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bukkit.Bukkit;
import org.bukkit.geometry.MutableVec3;
import org.bukkit.geometry.Vec3;

public abstract class MutableBlockIterator implements Iterator<Vec3> {

    private final MutableVec3 value = Bukkit.vectors().coarseMutableZero();
    private final Vec3 view = value.unmodifiable();

    protected abstract void advance(MutableVec3 value) throws NoSuchElementException;

    @Override
    public final Vec3 next() {
        advance(value);
        return view;
    }
}
