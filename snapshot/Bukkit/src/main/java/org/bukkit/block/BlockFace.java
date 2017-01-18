package org.bukkit.block;

import java.util.Arrays;
import java.util.List;

import org.bukkit.geometry.MutableVec3;
import org.bukkit.geometry.Vec3;
import org.bukkit.geometry.Vec3Coarse;
import org.bukkit.util.ImVector;
import org.bukkit.util.NumberConversions;

/**
 * Represents the face of a block
 */
public enum BlockFace implements Vec3Coarse<Vec3> {
    NORTH(8, 0, 0, -1),
    EAST(12, 1, 0, 0),
    SOUTH(0, 0, 0, 1),
    WEST(4, -1, 0, 0),
    UP(-1, 0, 1, 0),
    DOWN(-1, 0, -1, 0),
    NORTH_EAST(10, NORTH, EAST),
    NORTH_WEST(6, NORTH, WEST),
    SOUTH_EAST(14, SOUTH, EAST),
    SOUTH_WEST(2, SOUTH, WEST),
    WEST_NORTH_WEST(5, WEST, NORTH_WEST),
    NORTH_NORTH_WEST(7, NORTH, NORTH_WEST),
    NORTH_NORTH_EAST(9, NORTH, NORTH_EAST),
    EAST_NORTH_EAST(11, EAST, NORTH_EAST),
    EAST_SOUTH_EAST(13, EAST, SOUTH_EAST),
    SOUTH_SOUTH_EAST(15, SOUTH, SOUTH_EAST),
    SOUTH_SOUTH_WEST(1, SOUTH, SOUTH_WEST),
    WEST_SOUTH_WEST(3, WEST, SOUTH_WEST),
    SELF(-1, 0, 0, 0);

    private final int blockYaw; // -1 for undefined
    private final Vec3 direction;

    BlockFace(final int blockYaw, final int modX, final int modY, final int modZ) {
        this.blockYaw = blockYaw;
        this.direction = ImVector.of(modX, modY, modZ);
    }

    BlockFace(final int blockYaw, final BlockFace face1, final BlockFace face2) {
        this.blockYaw = blockYaw;
        this.direction = face1.normal().plus(face2.normal());
    }

    public boolean isHorizontal() {
        return blockYaw >= 0;
    }

    /**
     * A number from 0 to 15 representing the yaw of this face.
     *
     * The number increases in the clockwise direction looking down,
     * and 0 is due {@link #SOUTH}.
     *
     * @throws UnsupportedOperationException if this face is not oriented horizontally
     */
    public int blockYaw() {
        if(blockYaw < 0) {
            throw new UnsupportedOperationException("Face " + this + " is not a horizontal direction");
        }
        return blockYaw;
    }

    /**
     * The yaw of this face in degrees, between -180 and +180
     */
    public float yaw() {
        return (blockYaw() + 8) * (360F / 16F) - 180F;
    }

    public Vec3 normal() {
        return direction;
    }

    /**
     * Get the amount of X-coordinates to modify to get the represented block
     *
     * @return Amount of X-coordinates to modify
     */
    public int getModX() {
        return direction.coarseX();
    }

    /**
     * Get the amount of Y-coordinates to modify to get the represented block
     *
     * @return Amount of Y-coordinates to modify
     */
    public int getModY() {
        return direction.coarseY();
    }

    /**
     * Get the amount of Z-coordinates to modify to get the represented block
     *
     * @return Amount of Z-coordinates to modify
     */
    public int getModZ() {
        return direction.coarseZ();
    }

    public BlockFace getOppositeFace() {
        switch (this) {
        case NORTH:
            return BlockFace.SOUTH;

        case SOUTH:
            return BlockFace.NORTH;

        case EAST:
            return BlockFace.WEST;

        case WEST:
            return BlockFace.EAST;

        case UP:
            return BlockFace.DOWN;

        case DOWN:
            return BlockFace.UP;

        case NORTH_EAST:
            return BlockFace.SOUTH_WEST;

        case NORTH_WEST:
            return BlockFace.SOUTH_EAST;

        case SOUTH_EAST:
            return BlockFace.NORTH_WEST;

        case SOUTH_WEST:
            return BlockFace.NORTH_EAST;

        case WEST_NORTH_WEST:
            return BlockFace.EAST_SOUTH_EAST;

        case NORTH_NORTH_WEST:
            return BlockFace.SOUTH_SOUTH_EAST;

        case NORTH_NORTH_EAST:
            return BlockFace.SOUTH_SOUTH_WEST;

        case EAST_NORTH_EAST:
            return BlockFace.WEST_SOUTH_WEST;

        case EAST_SOUTH_EAST:
            return BlockFace.WEST_NORTH_WEST;

        case SOUTH_SOUTH_EAST:
            return BlockFace.NORTH_NORTH_WEST;

        case SOUTH_SOUTH_WEST:
            return BlockFace.NORTH_NORTH_EAST;

        case WEST_SOUTH_WEST:
            return BlockFace.EAST_NORTH_EAST;

        case SELF:
            return BlockFace.SELF;
        }

        return BlockFace.SELF;
    }

    private static final List<BlockFace> HORIZONTAL;

    static {
        final BlockFace[] array = new BlockFace[16];
        for(BlockFace face : values()) {
            if(face.isHorizontal()) {
                array[face.blockYaw()] = face;
            }
        }
        HORIZONTAL = Arrays.asList(array);
    }

    private static final BlockFace[] DIAGONAL = new BlockFace[] {
        SOUTH_EAST,
        SOUTH_WEST,
        NORTH_EAST,
        NORTH_WEST,
    };

    private static final BlockFace[] DIAGONAL_Z = new BlockFace[] {
        SOUTH_SOUTH_EAST,
        SOUTH_SOUTH_WEST,
        NORTH_NORTH_EAST,
        NORTH_NORTH_WEST,
    };

    private static final BlockFace[] DIAGONAL_X = new BlockFace[] {
        EAST_SOUTH_EAST,
        WEST_SOUTH_WEST,
        EAST_NORTH_EAST,
        WEST_NORTH_WEST,
    };

    /**
     * All horizontally oriented faces, starting with {@link #SOUTH}
     * and increasing clockwise looking down.
     */
    public static List<BlockFace> horizontal() {
        return HORIZONTAL;
    }

    /**
     * The horizontal face representing the given yaw direction.
     *
     * The yaw is wrapped to fit in the range 0 to 15.
     */
    public static BlockFace byBlockYaw(int yaw) {
        return HORIZONTAL.get(NumberConversions.mod(yaw, 16));
    }

    /**
     * Return the horizontal face that is closest to the given yaw in degrees.
     */
    public static BlockFace byYaw(float degrees) {
        return byBlockYaw(Math.round(degrees * (16F / 360F)));
    }

    public static BlockFace byDirection(Vec3 direction) {
        if(direction instanceof BlockFace) return (BlockFace) direction;
        return byDirection(direction.fineX(),
                           direction.fineY(),
                           direction.fineZ());
    }

    public static BlockFace byDirection(double x, double y, double z) {
        if(y != 0) {
            // vertical
            if(x != 0 || z != 0) {
                throw new IllegalArgumentException("No " + BlockFace.class.getSimpleName() +
                                                   " for direction " + x + ", " + y + ", " + z);
            }
            return y > 0 ? UP : DOWN;
        } else if(z == 0) {
            // on X axis (including origin)
            return x > 0 ? EAST : x < 0 ? WEST : SELF;
        } else if(x == 0) {
            // on Z axis
            return z > 0 ? SOUTH : NORTH;
        } else {
            // diagonal
            int quadrant = 0;
            if(z < 0) quadrant += 2;
            if(x < 0) quadrant += 1;

            final double ax = Math.abs(x);
            final double az = Math.abs(z);

            if(ax > az) {
                // X major
                return DIAGONAL_X[quadrant];
            } else if(ax < az) {
                // Z major
                return DIAGONAL_Z[quadrant];
            } else {
                // 45 degrees
                return DIAGONAL[quadrant];
            }
        }
    }

    @Override
    public int coarseX() {
        return direction.coarseX();
    }

    @Override
    public int coarseY() {
        return direction.coarseY();
    }

    @Override
    public int coarseZ() {
        return direction.coarseZ();
    }

    @Override
    public BlockFace copy() {
        return this;
    }

    @Override
    public MutableVec3 mutableCopy() {
        return direction.mutableCopy();
    }

    @Override
    public Vec3 fineCopy() {
        return direction.fineCopy();
    }

    @Override
    public Vec3 fineOf(double x, double y, double z) {
        return direction.fineOf(x, y, z);
    }

    @Override
    public Vec3 fineZero() {
        return direction.fineZero();
    }

    @Override
    public Vec3 coarseOf(int x, int y, int z) {
        return direction.coarseOf(x, y, z);
    }

    @Override
    public Vec3 coarseZero() {
        return direction.coarseZero();
    }
}
