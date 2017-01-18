package org.bukkit.util;

import java.util.Collection;
import java.util.EnumSet;

public final class EnumUtils {
    private EnumUtils() {}

    /**
     * Create a new {@link EnumSet} of the given type, copying the initial
     * contents from the given set.
     *
     * Unlike {@link EnumSet#copyOf(Collection)}, this method always works,
     * even if the given set is empty, and is not another {@link EnumSet}.
     */
    public static <E extends Enum<E>> EnumSet<E> copySet(Class<E> type, Collection<E> set) {
        final EnumSet<E> copy = EnumSet.noneOf(type);
        copy.addAll(set);
        return copy;
    }
}
