package org.bukkit.geometry;

import org.bukkit.Bukkit;

public class Direction {

    private static final double TWO_PI = Math.PI * 2, HALF_PI = Math.PI / 2;

    private final double yaw, pitch;

    private Direction(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double yaw() {
        return yaw;
    }

    public double pitch() {
        return pitch;
    }

    public float yawDegrees() {
        return (float) Math.toDegrees(yaw);
    }

    public float pitchDegrees() {
        return (float) Math.toDegrees(pitch);
    }

    public Vec3 toVector() {
        final double cos = Math.cos(pitch);
        return Bukkit.vectors().fine(
            -cos * Math.sin(yaw),
            -Math.sin(pitch),
            cos * Math.cos(yaw)
        );
    }

    public static Direction of(double yaw, double pitch) {
        return new Direction((yaw + TWO_PI) % TWO_PI, pitch);
    }

    public static Direction ofDegrees(double yaw, double pitch) {
        return of(Math.toRadians(yaw), Math.toRadians(pitch));
    }

    public static Direction fromVector(double x, double y, double z) {
        if (x == 0 && z == 0) {
            return new Direction(0, y > 0 ? -HALF_PI : HALF_PI);
        }

        final double xz = Math.sqrt(x * x + z * z);
        return of(Math.atan2(-x, z), Math.atan(-y / xz));
    }

    @Override
    public int hashCode() {
        return Double.hashCode(yaw) * 31 + Double.hashCode(pitch);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Direction)) return false;
        final Direction that = (Direction) obj;
        return this.yaw == that.yaw() &&
               this.pitch == that.pitch();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{yaw=" + yaw +
               " pitch=" + pitch +
               "}";
    }
}
