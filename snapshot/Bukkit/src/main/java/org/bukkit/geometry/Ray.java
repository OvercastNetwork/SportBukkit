package org.bukkit.geometry;

import java.util.Objects;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.util.ImVector;

public class Ray {

    private final ImVector origin;
    private final ImVector normal;

    private Ray(Vec3 origin, Vec3 normal) {
        this.origin = ImVector.copyOf(origin);
        this.normal = ImVector.copyOf(normal.unit());
    }

    public static Ray fromOriginAndNormal(Vec3 origin, Vec3 normal) {
        Preconditions.checkArgument(!normal.isZero(), "Ray normal must have non-zero length");
        return new Ray(origin, normal);
    }

    public static Ray fromOriginAndTarget(Vec3 origin, Vec3 target) {
        final Vec3 normal = target.minus(origin);
        Preconditions.checkArgument(!normal.isZero(), "Ray target must be different from origin");
        return new Ray(origin, normal);
    }

    public static Ray fromOriginAndDirection(Vec3 origin, Direction direction) {
        return new Ray(origin, direction.toVector());
    }

    public static Ray fromLocation(Location location) {
        return new Ray(location.toVector(), location.getDirection());
    }

    public ImVector origin() {
        return origin;
    }

    public ImVector normal() {
        return normal;
    }

    public Direction direction() {
        return normal.direction();
    }

    public ImVector atDistance(double distance) {
        return origin.plus(normal.times(distance));
    }

    public Ray translate(Vec3 offset) {
        return new Ray(origin.plus(offset), normal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, normal);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Ray)) return false;
        final Ray that = (Ray) obj;
        return origin.equals(that.origin) &&
               normal.equals(that.normal);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{origin=" + origin +
               " normal=" + normal +
               "}";
    }
}
