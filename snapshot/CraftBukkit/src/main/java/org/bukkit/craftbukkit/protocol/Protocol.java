package org.bukkit.craftbukkit.protocol;

import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class Protocol {

    public static final int LATEST = 316;
    public static final Set<Integer> SUPPORTED = ImmutableSet.of(107, 108, 109, 110, 201, 202, 203, 204, 205, 210, 315, 316);

    public static Set<Integer> supported(Predicate<Integer> filter) {
        return supported((com.google.common.base.Predicate<Integer>) filter::test);
    }

    public static Set<Integer> supported(com.google.common.base.Predicate<Integer> filter) {
        return Sets.filter(SUPPORTED, filter);
    }

}
