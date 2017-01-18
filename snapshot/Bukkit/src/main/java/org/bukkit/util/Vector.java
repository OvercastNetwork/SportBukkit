package org.bukkit.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.geometry.Cuboid;
import org.bukkit.geometry.Vec3;

/**
 * Represents a mutable vector. Because the components of Vectors are mutable,
 * storing Vectors long term may be dangerous if passing code modifies the
 * Vector later. If you want to keep around a Vector, it may be wise to call
 * <code>clone()</code> in order to get a copy.
 */
@SerializableAs("Vector")
public class Vector extends VectorBase<Vector> implements Cloneable, ConfigurationSerializable {
    private static final long serialVersionUID = -2657651106777219169L;

    private static Random random = new Random();

    /**
     * Threshold for fuzzy equals().
     */
    private static final double epsilon = 0.000001;

    /**
     * Construct the vector with all components as 0.
     */
    public Vector() {
        super(0, 0, 0);
    }

    /**
     * Construct the vector with provided integer components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public Vector(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * Construct the vector with provided double components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Construct the vector with all components set to the given value
     */
    public Vector(double n) {
        super(n, n, n);
    }

    /**
     * Construct the vector with provided float components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public Vector(float x, float y, float z) {
        super(x, y, z);
    }

    /**
     * Construct a copy of the given Vector
     */
    public Vector(Vec3 that) {
        super(that.fineX(), that.fineY(), that.fineZ());
    }

    @Override
    public Vector mutableCopy() {
        return new Vector(this);
    }

    @Override
    public Vec3 coarseOf(int x, int y, int z) {
        return Bukkit.vectors().coarse(x, y, z);
    }

    @Override
    public Vec3 coarseZero() {
        return Bukkit.vectors().coarseZero();
    }

    @Override
    public ImVector fineOf(double x, double y, double z) {
        return ImVector.of(x, y, z);
    }

    @Override
    public ImVector fineZero() {
        return ImVector.ofZero();
    }

    public Vector clamp(Cuboid bounds) {
        return clamp(bounds.minimum(), bounds.maximum());
    }

    @Override
    public Vec3 unmodifiable() {
        return new ImVector(0, 0, 0) {
            @Override
            public double fineX() {
                return Vector.this.fineX();
            }

            @Override
            public double fineY() {
                return Vector.this.fineY();
            }

            @Override
            public double fineZ() {
                return Vector.this.fineZ();
            }
        };
    }

    /**
     * Adds a vector to this one
     *
     * @param vec The other vector
     * @return the same vector
     */
    public Vector add(Vector vec) {
        return add((Vec3) vec);
    }

    /**
     * Subtracts a vector from this one.
     *
     * @param vec The other vector
     * @return the same vector
     */
    public Vector subtract(Vector vec) {
        return subtract((Vec3) vec);
    }

    /**
     * Multiplies the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    public Vector multiply(Vector vec) {
        return set(this.fineX() * vec.fineX(), this.fineY() * vec.fineY(), this.fineZ() * vec.fineZ());
    }

    /**
     * Divides the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    public Vector divide(Vector vec) {
        return set(this.fineX() / vec.fineX(), this.fineY() / vec.fineY(), this.fineZ() / vec.fineZ());
    }

    /**
     * Copies another vector
     *
     * @param vec The other vector
     * @return the same vector
     */
    public Vector copy(Vector vec) {
        return set(vec);
    }

    /**
     * Gets the magnitude of the vector, defined as sqrt(x^2+y^2+z^2). The
     * value of this method is not cached and uses a costly square-root
     * function, so do not repeatedly call this method to get the vector's
     * magnitude. NaN will be returned if the inner result of the sqrt()
     * function overflows, which will be caused if the length is too long.
     *
     * @return the magnitude
     */
    public double length() {
        return super.length();
    }

    /**
     * Gets the magnitude of the vector squared.
     *
     * @return the magnitude
     */
    public double lengthSquared() {
        return super.lengthSquared();
    }

    /**
     * Get the distance between this vector and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the vector's magnitude. NaN will be
     * returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param o The other vector
     * @return the distance
     */
    public double distance(Vector o) {
        return distance((Vec3) o);
    }

    /**
     * Get the squared distance between this vector and another.
     *
     * @param o The other vector
     * @return the distance
     */
    public double distanceSquared(Vector o) {
        return distanceSquared((Vec3) o);
    }

    /**
     * Gets the angle between this vector and another in radians.
     *
     * @param other The other vector
     * @return angle in radians
     */
    public float angle(Vector other) {
        double dot = dot(other) / (length() * other.length());

        return (float) Math.acos(dot);
    }

    /**
     * Sets this vector to the midpoint between this vector and another.
     *
     * @param other The other vector
     * @return this same vector (now a midpoint)
     */
    public Vector midpoint(Vector other) {
        return set((fineX() + other.fineX()) / 2,
                   (fineY() + other.fineY()) / 2,
                   (fineZ() + other.fineZ()) / 2);
    }

    /**
     * Gets a new midpoint vector between this vector and another.
     *
     * @param other The other vector
     * @return a new midpoint vector
     */
    public Vector getMidpoint(Vector other) {
        double x = (this.fineX() + other.fineX()) / 2;
        double y = (this.fineY() + other.fineY()) / 2;
        double z = (this.fineZ() + other.fineZ()) / 2;
        return new Vector(x, y, z);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    public Vector multiply(int m) {
        return set(fineX() * m, fineY() * m, fineZ() * m);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    public Vector multiply(double m) {
        return set(fineX() * m, fineY() * m, fineZ() * m);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    public Vector multiply(float m) {
        return set(fineX() * m, fineY() * m, fineZ() * m);
    }

    public Vector divide(double m) {
        return set(fineX() / m, fineY() / m, fineZ() / m);
    }

    /**
     * Calculates the dot product of this vector with another. The dot product
     * is defined as x1*x2+y1*y2+z1*z2. The returned value is a scalar.
     *
     * @param other The other vector
     * @return dot product
     */
    public double dot(Vector other) {
        return dot((Vec3) other);
    }

    /**
     * Calculates the cross product of this vector with another. The cross
     * product is defined as:
     * <ul>
     * <li>x = y1 * z2 - y2 * z1
     * <li>y = z1 * x2 - z2 * x1
     * <li>z = x1 * y2 - x2 * y1
     * </ul>
     *
     * @param o The other vector
     * @return the same vector
     */
    public Vector crossProduct(Vector o) {
        return set(fineY() * o.fineZ() - o.fineY() * fineZ(),
                   fineZ() * o.fineX() - o.fineZ() * fineX(),
                   fineX() * o.fineY() - o.fineX() * fineY());
    }

    /**
     * Calculates the cross product of this vector with another without mutating
     * the original. The cross product is defined as:
     * <ul>
     * <li>x = y1 * z2 - y2 * z1
     * <li>y = z1 * x2 - z2 * x1
     * <li>z = x1 * y2 - x2 * y1
     * </ul>
     *
     * @param o The other vector
     * @return a new vector
     */
    public Vector getCrossProduct(Vector o) {
        double x = this.fineY() * o.fineZ() - o.fineY() * this.fineZ();
        double y = this.fineZ() * o.fineX() - o.fineZ() * this.fineX();
        double z = this.fineX() * o.fineY() - o.fineX() * this.fineY();
        return new Vector(x, y, z);
    }

    /**
     * Converts this vector to a unit vector (a vector with length of 1).
     *
     * @return the same vector
     */
    public Vector normalize() {
        return divide(length());
    }

    /**
     * Zero this vector's components.
     *
     * @return the same vector
     */
    public Vector zero() {
        return setZero();
    }

    /**
     * Returns whether this vector is in an axis-aligned bounding box.
     * <p>
     * The minimum and maximum vectors given must be truly the minimum and
     * maximum X, Y and Z components.
     *
     * @param min Minimum vector
     * @param max Maximum vector
     * @return whether this vector is in the AABB
     */
    public boolean isInAABB(Vector min, Vector max) {
        return fineX() >= min.fineX() && fineX() <= max.fineX() && fineY() >= min.fineY() && fineY() <= max.fineY() && fineZ() >= min.fineZ() && fineZ() <= max.fineZ();
    }

    /**
     * Returns whether this vector is within a sphere.
     *
     * @param origin Sphere origin.
     * @param radius Sphere radius
     * @return whether this vector is in the sphere
     */
    public boolean isInSphere(Vector origin, double radius) {
        return (NumberConversions.square(origin.fineX() - fineX()) + NumberConversions.square(origin.fineY() - fineY()) + NumberConversions.square(origin.fineZ() - fineZ())) <= NumberConversions.square(radius);
    }

    /**
     * Gets the X component.
     *
     * @return The X component.
     */
    public double getX() {
        return fineX();
    }

    /**
     * Gets the floored value of the X component, indicating the block that
     * this vector is contained with.
     *
     * @return block X
     */
    public int getBlockX() {
        return coarseX();
    }

    /**
     * Gets the Y component.
     *
     * @return The Y component.
     */
    public double getY() {
        return fineY();
    }

    /**
     * Gets the floored value of the Y component, indicating the block that
     * this vector is contained with.
     *
     * @return block y
     */
    public int getBlockY() {
        return coarseY();
    }

    /**
     * Gets the Z component.
     *
     * @return The Z component.
     */
    public double getZ() {
        return fineZ();
    }

    /**
     * Gets the floored value of the Z component, indicating the block that
     * this vector is contained with.
     *
     * @return block z
     */
    public int getBlockZ() {
        return coarseZ();
    }

    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    @Override
    public Vector setX(int x) {
        setX((double) x);
        return this;
    }

    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    @Override
    public Vector setX(double x) {
        super.setX(x);
        return this;
    }

    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    public Vector setX(float x) {
        setX((double) x);
        return this;
    }

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    @Override
    public Vector setY(int y) {
        setY((double) y);
        return this;
    }

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    @Override
    public Vector setY(double y) {
        super.setY(y);
        return this;
    }

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    public Vector setY(float y) {
        setY((double) y);
        return this;
    }

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    @Override
    public Vector setZ(int z) {
        setZ((double) z);
        return this;
    }

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    @Override
    public Vector setZ(double z) {
        super.setZ(z);
        return this;
    }

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    public Vector setZ(float z) {
        setZ((double) z);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Vec3 && equals((Vec3) obj));
    }

    /**
     * Returns a hash code for this vector
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return fineHashCode();
    }

    /**
     * Get a new vector.
     *
     * @return vector
     */
    @Override
    public Vector clone() {
        try {
            return (Vector) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    /**
     * Returns this vector's components as x,y,z.
     */
    @Override
    public String toString() {
        return fineX() + "," + fineY() + "," + fineZ();
    }

    /**
     * Gets a Location version of this vector with yaw and pitch being 0.
     *
     * @param world The world to link the location to.
     * @return the location
     */
    public Location toLocation(World world) {
        return new Location(world, fineX(), fineY(), fineZ());
    }

    /**
     * Gets a Location version of this vector.
     *
     * @param world The world to link the location to.
     * @param yaw The desired yaw.
     * @param pitch The desired pitch.
     * @return the location
     */
    public Location toLocation(World world, float yaw, float pitch) {
        return new Location(world, fineX(), fineY(), fineZ(), yaw, pitch);
    }

    /**
     * Get the block vector of this vector.
     *
     * @return A block vector.
     */
    public BlockVector toBlockVector() {
        return new BlockVector(fineX(), fineY(), fineZ());
    }

    /**
     * Get the threshold used for equals().
     *
     * @return The epsilon.
     */
    public static double getEpsilon() {
        return epsilon;
    }

    /**
     * Gets the minimum components of two vectors.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return minimum
     */
    public static Vector getMinimum(Vector v1, Vector v2) {
        return new Vector(Math.min(v1.fineX(), v2.fineX()), Math.min(v1.fineY(), v2.fineY()), Math.min(v1.fineZ(), v2.fineZ()));
    }

    /**
     * Gets the maximum components of two vectors.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return maximum
     */
    public static Vector getMaximum(Vector v1, Vector v2) {
        return new Vector(Math.max(v1.fineX(), v2.fineX()), Math.max(v1.fineY(), v2.fineY()), Math.max(v1.fineZ(), v2.fineZ()));
    }

    public static Vector minimum(Vec3 a, Vec3 b) {
        return new Vector(Math.min(a.fineX(), b.fineX()),
                          Math.min(a.fineY(), b.fineY()),
                          Math.min(a.fineZ(), b.fineZ()));
    }

    public static Vector maximum(Vec3 a, Vec3 b) {
        return new Vector(Math.max(a.fineX(), b.fineX()),
                          Math.max(a.fineY(), b.fineY()),
                          Math.max(a.fineZ(), b.fineZ()));
    }

    /**
     * Gets a random vector with components having a random value between 0
     * and 1.
     *
     * @return A random vector.
     */
    public static Vector getRandom() {
        return new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("x", getX());
        result.put("y", getY());
        result.put("z", getZ());

        return result;
    }

    public static Vector deserialize(Map<String, Object> args) {
        double x = 0;
        double y = 0;
        double z = 0;

        if (args.containsKey("x")) {
            x = (Double) args.get("x");
        }
        if (args.containsKey("y")) {
            y = (Double) args.get("y");
        }
        if (args.containsKey("z")) {
            z = (Double) args.get("z");
        }

        return new Vector(x, y, z);
    }
}
