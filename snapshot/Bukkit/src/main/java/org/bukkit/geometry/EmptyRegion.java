package org.bukkit.geometry;

import java.util.Random;

import org.bukkit.region.BlockRegion;
import org.bukkit.util.ImVector;

public interface EmptyRegion extends Region {

    default int standardHashCode() {
        return 0;
    }

    default boolean standardEquals(Object obj) {
        return obj instanceof Region && ((Region) obj).isEmpty();
    }

    @Override
    default boolean isEmpty() {
        return true;
    }

    @Override
    default boolean isBlockEmpty() {
        return true;
    }

    @Override
    default boolean isFinite() {
        return true;
    }

    @Override
    default boolean isBlockFinite() {
        return true;
    }

    @Override
    default double volume() {
        return 0;
    }

    @Override
    default int blockVolume() {
        return 0;
    }

    @Override
    default boolean contains(Vec3 point) {
        return false;
    }

    @Override
    default boolean containsBlock(Vec3 v) {
        return false;
    }

    @Override
    default Vec3 randomPointInside(Random random) {
        return ImVector.ofNaN();
    }

    @Override
    default Vec3 randomBlockInside(Random random) {
        throw new ArithmeticException("Region is empty");
    }

    @Override
    default Cuboid bounds() {
        return Cuboid.empty();
    }

    @Override
    default BlockRegion blockRegion() {
        return BlockRegion.empty();
    }

    @Override
    default Region transform(Transform transform) {
        return this;
    }
}
