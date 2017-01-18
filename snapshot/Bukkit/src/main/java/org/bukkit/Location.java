package org.bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.geometry.Direction;
import org.bukkit.geometry.Ray;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;

import org.bukkit.geometry.Vec3;
import org.bukkit.util.Vector;

/**
 * Represents a 3-dimensional position in a world
 */
public class Location implements Cloneable, ConfigurationSerializable, Locatable {

    private UUID worldId;
    private final Vector position;
    private float pitch;
    private float yaw;

    public Location(World world, Vec3 position) {
        this(world, position, 0, 0);
    }

    public Location(UUID world, Vec3 position) {
        this(world, position, 0, 0);
    }

    public Location(World world, Vec3 position, float yaw, float pitch) {
        this(world, position.fineX(), position.fineY(), position.fineZ(), yaw, pitch);
    }

    public Location(UUID world, Vec3 position, float yaw, float pitch) {
        this(world, position.fineX(), position.fineY(), position.fineZ(), yaw, pitch);
    }

    public Location(World world, Vec3 position, Direction direction) {
        this(world, position.fineX(), position.fineY(), position.fineZ(), direction.yawDegrees(), direction.pitchDegrees());
    }

    public Location(UUID world, Vec3 position, Direction direction) {
        this(world, position.fineX(), position.fineY(), position.fineZ(), direction.yawDegrees(), direction.pitchDegrees());
    }

    public Location(World world, Ray ray) {
        this(world, ray.origin().fineX(), ray.origin().fineY(), ray.origin().fineZ(),
             ray.direction().yawDegrees(), ray.direction().pitchDegrees());
    }

    public Location(UUID world, Ray ray) {
        this(world, ray.origin().fineX(), ray.origin().fineY(), ray.origin().fineZ(),
             ray.direction().yawDegrees(), ray.direction().pitchDegrees());
    }

    /**
     * Constructs a new Location with the given coordinates
     *
     * @param world The world in which this location resides
     * @param x The x-coordinate of this new location
     * @param y The y-coordinate of this new location
     * @param z The z-coordinate of this new location
     */
    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public Location(UUID world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    /**
     * Constructs a new Location with the given coordinates and direction
     *
     * @param world The world in which this location resides
     * @param x The x-coordinate of this new location
     * @param y The y-coordinate of this new location
     * @param z The z-coordinate of this new location
     * @param yaw The absolute rotation on the x-plane, in degrees
     * @param pitch The absolute rotation on the y-plane, in degrees
     */
    public Location(World world, double x, double y, double z, float yaw, float pitch) {
        this(world.getUID(), x, y, z, yaw, pitch);
    }

    public Location(UUID world, double x, double y, double z, float yaw, float pitch) {
        this.worldId = world;
        this.position = new Vector(x, y, z);
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void setWorldId(UUID worldId) {
        this.worldId = worldId;
    }

    @Override
    public UUID getWorldId() {
        return worldId;
    }

    /**
     * Sets the world that this location resides in
     *
     * @param world New world that this location resides in
     */
    public void setWorld(World world) {
        setWorldId(world.getUID());
    }

    /**
     * Gets the world that this location resides in
     *
     * @return World that contains this location
     */
    @Override
    public World getWorld() {
        return Bukkit.world(getWorldId());
    }

    @Override
    public Location getLocation() {
        return this;
    }

    /**
     * Gets the chunk at the represented location
     *
     * @return Chunk at the represented location
     */
    public Chunk getChunk() {
        return getWorld().getChunkAt(this);
    }

    /**
     * Gets the block at the represented location
     *
     * @return Block at the represented location
     */
    public Block getBlock() {
        return getWorld().getBlockAt(this);
    }

    public Vector position() {
        return position;
    }

    public void setPosition(Vec3 pos) {
        position.set(pos);
    }

    public void setPosition(double x, double y, double z) {
        position.set(x, y, z);
    }

    /**
     * Sets the x-coordinate of this location
     *
     * @param x X-coordinate
     */
    public void setX(double x) {
        position.setX(x);
    }

    /**
     * Gets the x-coordinate of this location
     *
     * @return x-coordinate
     */
    public double getX() {
        return position.fineX();
    }

    /**
     * Gets the floored value of the X component, indicating the block that
     * this location is contained with.
     *
     * @return block X
     */
    public int getBlockX() {
        return position.coarseX();
    }

    /**
     * Sets the y-coordinate of this location
     *
     * @param y y-coordinate
     */
    public void setY(double y) {
        position.setY(y);
    }

    /**
     * Gets the y-coordinate of this location
     *
     * @return y-coordinate
     */
    public double getY() {
        return position.fineY();
    }

    /**
     * Gets the floored value of the Y component, indicating the block that
     * this location is contained with.
     *
     * @return block y
     */
    public int getBlockY() {
        return position.coarseY();
    }

    /**
     * Sets the z-coordinate of this location
     *
     * @param z z-coordinate
     */
    public void setZ(double z) {
        position.setZ(z);
    }

    /**
     * Gets the z-coordinate of this location
     *
     * @return z-coordinate
     */
    public double getZ() {
        return position.fineZ();
    }

    /**
     * Gets the floored value of the Z component, indicating the block that
     * this location is contained with.
     *
     * @return block z
     */
    public int getBlockZ() {
        return position.coarseZ();
    }

    /**
     * Sets the yaw of this location, measured in degrees.
     * <ul>
     * <li>A yaw of 0 or 360 represents the positive z direction.
     * <li>A yaw of 180 represents the negative z direction.
     * <li>A yaw of 90 represents the negative x direction.
     * <li>A yaw of 270 represents the positive x direction.
     * </ul>
     * Increasing yaw values are the equivalent of turning to your
     * right-facing, increasing the scale of the next respective axis, and
     * decreasing the scale of the previous axis.
     *
     * @param yaw new rotation's yaw
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Gets the yaw of this location, measured in degrees.
     * <ul>
     * <li>A yaw of 0 or 360 represents the positive z direction.
     * <li>A yaw of 180 represents the negative z direction.
     * <li>A yaw of 90 represents the negative x direction.
     * <li>A yaw of 270 represents the positive x direction.
     * </ul>
     * Increasing yaw values are the equivalent of turning to your
     * right-facing, increasing the scale of the next respective axis, and
     * decreasing the scale of the previous axis.
     *
     * @return the rotation's yaw
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the pitch of this location, measured in degrees.
     * <ul>
     * <li>A pitch of 0 represents level forward facing.
     * <li>A pitch of 90 represents downward facing, or negative y
     *     direction.
     * <li>A pitch of -90 represents upward facing, or positive y direction.
     * </ul>
     * Increasing pitch values the equivalent of looking down.
     *
     * @param pitch new incline's pitch
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Gets the pitch of this location, measured in degrees.
     * <ul>
     * <li>A pitch of 0 represents level forward facing.
     * <li>A pitch of 90 represents downward facing, or negative y
     *     direction.
     * <li>A pitch of -90 represents upward facing, or positive y direction.
     * </ul>
     * Increasing pitch values the equivalent of looking down.
     *
     * @return the incline's pitch
     */
    public float getPitch() {
        return pitch;
    }

    public Direction direction() {
        return Direction.ofDegrees(yaw, pitch);
    }

    public Ray ray() {
        return Ray.fromLocation(this);
    }

    /**
     * Gets a unit-vector pointing in the direction that this Location is
     * facing.
     *
     * @return a vector pointing the direction of this location's {@link
     *     #getPitch() pitch} and {@link #getYaw() yaw}
     */
    public Vector getDirection() {
        Vector vector = new Vector();

        double rotX = this.getYaw();
        double rotY = this.getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double xz = Math.cos(Math.toRadians(rotY));

        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

        return vector;
    }

    /**
     * Sets the {@link #getYaw() yaw} and {@link #getPitch() pitch} to point
     * in the direction of the vector.
     * 
     * @param vector the direction vector
     * @return the same location
     */
    public Location setDirection(Vector vector) {
        /*
         * Sin = Opp / Hyp
         * Cos = Adj / Hyp
         * Tan = Opp / Adj
         *
         * x = -Opp
         * z = Adj
         */
        final double _2PI = 2 * Math.PI;
        final double x = vector.getX();
        final double z = vector.getZ();

        if (x == 0 && z == 0) {
            pitch = vector.getY() > 0 ? -90 : 90;
            return this;
        }

        double theta = Math.atan2(-x, z);
        yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);

        double x2 = NumberConversions.square(x);
        double z2 = NumberConversions.square(z);
        double xz = Math.sqrt(x2 + z2);
        pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));

        return this;
    }

    /**
     * Adds the location by another.
     *
     * @see Vector
     * @param vec The other location
     * @return the same location
     * @throws IllegalArgumentException for differing worlds
     */
    public Location add(Location vec) {
        if (vec == null || vec.getWorld() != getWorld()) {
            throw new IllegalArgumentException("Cannot add Locations of differing worlds");
        }
        return add(vec.position());
    }

    /**
     * Adds the location by a vector.
     *
     * @see Vector
     * @param vec Vector to use
     * @return the same location
     */
    public Location add(Vector vec) {
        position.add(vec);
        return this;
    }

    /**
     * Adds the location by another. Not world-aware.
     *
     * @see Vector
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return the same location
     */
    public Location add(double x, double y, double z) {
        position.add(x, y, z);
        return this;
    }

    /**
     * Subtracts the location by another.
     *
     * @see Vector
     * @param vec The other location
     * @return the same location
     * @throws IllegalArgumentException for differing worlds
     */
    public Location subtract(Location vec) {
        if (vec == null || vec.getWorld() != getWorld()) {
            throw new IllegalArgumentException("Cannot add Locations of differing worlds");
        }
        return subtract(vec.position());
    }

    /**
     * Subtracts the location by a vector.
     *
     * @see Vector
     * @param vec The vector to use
     * @return the same location
     */
    public Location subtract(Vector vec) {
        position.subtract(vec);
        return this;
    }

    /**
     * Subtracts the location by another. Not world-aware and
     * orientation independent.
     *
     * @see Vector
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return the same location
     */
    public Location subtract(double x, double y, double z) {
        position.subtract(x, y, z);
        return this;
    }

    /**
     * Gets the magnitude of the location, defined as sqrt(x^2+y^2+z^2). The
     * value of this method is not cached and uses a costly square-root
     * function, so do not repeatedly call this method to get the location's
     * magnitude. NaN will be returned if the inner result of the sqrt()
     * function overflows, which will be caused if the length is too long. Not
     * world-aware and orientation independent.
     *
     * @see Vector
     * @return the magnitude
     */
    public double length() {
        return position.length();
    }

    /**
     * Gets the magnitude of the location squared. Not world-aware and
     * orientation independent.
     *
     * @see Vector
     * @return the magnitude
     */
    public double lengthSquared() {
        return position.lengthSquared();
    }

    /**
     * Get the distance between this location and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the location's magnitude. NaN will
     * be returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @see Vector
     * @param o The other location
     * @return the distance
     * @throws IllegalArgumentException for differing worlds
     */
    public double distance(Location o) {
        return Math.sqrt(distanceSquared(o));
    }

    /**
     * Get the squared distance between this location and another.
     *
     * @see Vector
     * @param o The other location
     * @return the distance
     * @throws IllegalArgumentException for differing worlds
     */
    public double distanceSquared(Location o) {
        if (o == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null location");
        } else if (o.getWorld() == null || getWorld() == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null world");
        } else if (o.getWorld() != getWorld()) {
            throw new IllegalArgumentException("Cannot measure distance between " + getWorld().getName() + " and " + o.getWorld().getName());
        }
        return position.distanceSquared(o.position());
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar. Not world-aware.
     *
     * @param m The factor
     * @see Vector
     * @return the same location
     */
    public Location multiply(double m) {
        position.multiply(m);
        return this;
    }

    /**
     * Copy yaw and pitch from the given {@link Location} to this one.
     * @return this object
     */
    public Location copyAngles(Location loc) {
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        return this;
    }

    /**
     * Copy position and angles from the given {@link Location} to this one.
     * @return this object
     */
    public Location copyLocation(Location loc) {
        position.set(loc.position());
        return copyAngles(loc);
    }

    /**
     * Zero this location's components. Not world-aware.
     *
     * @see Vector
     * @return the same location
     */
    public Location zero() {
        position.setZero();
        return this;
    }

    @Override
    public final boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof Location)) return false;
        final Location that = (Location) obj;
        return Objects.equals(worldId, that.getWorldId()) &&
               Objects.equals(position, that.position()) &&
               yaw == that.getYaw() &&
               pitch == that.getPitch();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(worldId, position, yaw, pitch);
    }

    @Override
    public String toString() {
        return "Location{" + "world=" + worldId + ",pos=" + position + ",pitch=" + pitch + ",yaw=" + yaw + '}';
    }

    /**
     * Constructs a new {@link Vector} based on this Location
     *
     * @return New Vector containing the coordinates represented by this
     *     Location
     */
    public Vector toVector() {
        return position.mutableCopy();
    }

    public BlockVector toBlockVector() {
        return position.toBlockVector();
    }

    @Override
    public Location clone() {
        return new Location(worldId, position, yaw, pitch);
    }

    @Utility
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("world", this.worldId);

		data.put("x", position.fineX());
		data.put("y", position.fineY());
		data.put("z", position.fineZ());

		data.put("yaw", this.yaw);
		data.put("pitch", this.pitch);

		return data;
	}
	
	 /**
     * Required method for deserialization
     *
     * @param args map to deserialize
     * @return deserialized location
     * @throws IllegalArgumentException if the world don't exists
     * @see ConfigurationSerializable
     */
	public static Location deserialize(Map<String, Object> args) {
		World world = Bukkit.getWorld((String) args.get("world"));
		if (world == null) {
			throw new IllegalArgumentException("unknown world");
		}

		return new Location(world, NumberConversions.toDouble(args.get("x")), NumberConversions.toDouble(args.get("y")), NumberConversions.toDouble(args.get("z")), NumberConversions.toFloat(args.get("yaw")), NumberConversions.toFloat(args.get("pitch")));
	}
}
