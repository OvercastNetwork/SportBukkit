package org.bukkit.geometry;

/**
 * Construct {@link Vec3} instances
 */
public interface VectorFactory {

    Vec3 coarse(int x, int y, int z);

    default Vec3 coarse(int xyz) {
        return coarse(xyz, xyz, xyz);
    }

    Vec3 coarse(double x, double y, double z);

    default Vec3 coarse(double xyz) {
        return coarse(xyz, xyz, xyz);
    }

    Vec3 fine(int x, int y, int z);

    default Vec3 fine(int xyz) {
        return fine(xyz, xyz, xyz);
    }

    Vec3 fine(double x, double y, double z);

    default Vec3 fine(double xyz) {
        return fine(xyz, xyz, xyz);
    }

    MutableVec3 coarseMutable(int x, int y, int z);

    default MutableVec3 coarseMutable(int xyz) {
        return coarseMutable(xyz, xyz, xyz);
    }

    MutableVec3 fineMutable(double x, double y, double z);

    default MutableVec3 fineMutable(double xyz) {
        return fineMutable(xyz, xyz, xyz);
    }

    default Vec3 coarseZero() {
        return coarse(0, 0, 0);
    }

    default Vec3 fineZero() {
        return fine(0, 0, 0);
    }

    default MutableVec3 coarseMutableZero() {
        return coarseMutable(0, 0, 0);
    }

    default MutableVec3 fineMutableZero() {
        return fineMutable(0, 0, 0);
    }

    default MutableVec3 coarseMutable(Vec3 v) {
        return coarseMutable(v.coarseX(), v.coarseY(), v.coarseZ());
    }

    default MutableVec3 fineMutable(Vec3 v) {
        return fineMutable(v.fineX(), v.fineY(), v.fineZ());
    }
}
