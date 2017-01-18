package org.bukkit.geometry;

class Nowhere implements EmptyRegion {

    static final Nowhere INSTANCE = new Nowhere();

    private Nowhere() {}
}
