package org.bukkit;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.util.EnumUtils;
import org.bukkit.util.Vector;

public class EntityLocation extends Location {

    private final Vector velocity;
    private final Set<PoseFlag> poseFlags;

    /**
     * Create a new EntityLocation from the given values.
     *
     * Note that velocity and poseFlags are NOT copied.
     */
    public EntityLocation(World world, double x, double y, double z, float yaw, float pitch, Vector velocity, Set<PoseFlag> poseFlags) {
        super(world, x, y, z, yaw, pitch);
        this.velocity = velocity;
        this.poseFlags = poseFlags;

    }

    public EntityLocation(World world, Vector position, float yaw, float pitch, Vector velocity, Set<PoseFlag> poseFlags) {
        this(world, position.getX(), position.getY(), position.getZ(), yaw, pitch, velocity, poseFlags);
    }

    /**
     * Create a new EntityLocation from the given values.
     *
     * Only the position and angles are copied from the location argument,
     * even if it is another EntityLocation.
     *
     * Note that velocity and poseFlags are NOT copied.
     */
    public EntityLocation(Location location, Vector velocity, Set<PoseFlag> poseFlags) {
        this(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), velocity, poseFlags);
    }

    /**
     * Create a new EntityLocation from the given values, with zero velocity and no {@link PoseFlag}s.
     */
    public EntityLocation(World world, double x, double y, double z, float yaw, float pitch) {
        this(world, x, y, z, yaw, pitch, new Vector(), EnumSet.noneOf(PoseFlag.class));
    }

    public static EntityLocation coerce(Location value, EntityLocation defaults) {
        if(value instanceof EntityLocation) {
            return (EntityLocation) value;
        } else {
            return new EntityLocation(value, defaults.velocity(), defaults.poseFlags());
        }
    }

    public static EntityLocation copyOf(Location location, Vector velocity, Set<PoseFlag> poseFlags) {
        return new EntityLocation(location, new Vector(velocity), EnumUtils.copySet(PoseFlag.class, poseFlags));
    }

    public static EntityLocation copyOf(EntityLocation that) {
        return copyOf(that, that.velocity(), that.poseFlags());
    }

    /**
     * Mutable velocity vector.
     *
     * Changes to the returned object are reflected in this {@link EntityLocation},
     * and vice-versa.
     */
    public Vector velocity() {
        return velocity;
    }

    /**
     * A mutable set of {@link PoseFlag}s.
     *
     * Changes to the returned object are reflected in this {@link EntityLocation},
     * and vice-versa.
     */
    public Set<PoseFlag> poseFlags() {
        return poseFlags;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
               "{world=" + getWorld() +
               " x=" + getX() +
               " y=" + getY() +
               " z=" + getZ() +
               " pitch=" + getPitch() +
               " yaw=" + getYaw() +
               " velocity=" + velocity() +
               " poseFlags=" + poseFlags() +
               '}';
    }

    @Override
    public EntityLocation clone() {
        // Can't call super.clone(), because we have final fields
        return copyOf(this);
    }
}
