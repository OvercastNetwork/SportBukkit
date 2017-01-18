package org.bukkit.geometry;

import java.util.Arrays;

import org.bukkit.Bukkit;

/**
 * Represents by a 4x4 matrix of the form:
 *
 *     [ xx  0   xz  xt ]
 *     [ 0   1   0   yt ]
 *     [ zx  0   zz  zt ]
 *     [ 0   0   0   1  ]
 *
 * Where
 *
 *     xt, yt, zt are integers
 *
 * and
 *
 *     |  xx  xz  |
 *     |          | = -1 or 1
 *     |  zx  zz  |
 *
 * i.e. the determinant of the x-z minor 2x2 matrix is -1 or 1,
 * which ensures that area is preserved in the x-z plane,
 * though orientation may be inverted.
 */
class BlockTransform implements CoarseTransform {

    private final int xx, xz,
                      zx, zz;
    private final int xt, yt, zt;

    // The x-z determinant, might come in handy
    private final int determinant;

    // Cached inverse
    private BlockTransform inverse;

    BlockTransform(int xx, int xz, int zx, int zz, int xt, int yt, int zt) {
        this(null, xx, xz, zx, zz, xt, yt, zt);
    }

    BlockTransform(BlockTransform inverse, int xx, int xz, int zx, int zz, int xt, int yt, int zt) {
        this.determinant = xx * zz - xz * zx;

        if(determinant != -1 && determinant != 1) {
            throw new IllegalArgumentException(
                "Invalid block transform - coefficients do not preserve area: [ " + xx + " " + xz + " ], [ " + zx + " " + zz + " ]"
            );
        }

        this.inverse = inverse;

        this.xx = xx; this.xz = xz;
        this.zx = zx; this.zz = zz;

        this.xt = xt; this.yt = yt; this.zt = zt;
    }

    @Override
    public boolean isIdentity() {
        return xx == 1 && xz == 0 &&
               zx == 0 && zz == 1 &&
               xt == 0 && yt == 0 && zt == 0;
    }

    @Override
    public Vec3 apply(Vec3 v) {
        if(v.isFine()) {
            final double x = v.fineX();
            final double y = v.fineY();
            final double z = v.fineZ();

            return Bukkit.vectors().fine(
                x * xx + z * xz + xt,
                y + yt,
                x * zx + z * zz + zt
            );
        } else {
            final int x = v.coarseX();
            final int y = v.coarseY();
            final int z = v.coarseZ();

            return Bukkit.vectors().coarse(
                x * xx + z * xz + xt,
                y + yt,
                x * zx + z * zz + zt
            );
        }
    }

    @Override
    public void applyInPlace(MutableVec3 v) {
        if(v.isFine()) {
            final double x = v.fineX();
            final double y = v.fineY();
            final double z = v.fineZ();

            v.set(
                x * xx + z * xz + xt,
                y + yt,
                x * zx + z * zz + zt
            );
        } else {
            final int x = v.coarseX();
            final int y = v.coarseY();
            final int z = v.coarseZ();

            v.set(
                x * xx + z * xz + xt,
                y + yt,
                x * zx + z * zz + zt
            );
        }
    }

    @Override
    public CoarseTransform inverse() {
        if(inverse == null) {
            inverse = new BlockTransform(
                this,
                xx, zx,
                xz, zz,
                - xx * xt - zx * zt,
                - yt,
                - xz * xt - zz * zt
            );
        }
        return inverse;
    }

    @Override
    public CoarseTransform compose(CoarseTransform before) {
        if(before instanceof BlockTransform) {
            final BlockTransform that = (BlockTransform) before;
            return new BlockTransform(
                this.xx * that.xx + this.xz * that.zx, this.xx * that.xz + this.xz * that.zz,
                this.zx * that.xx + this.zz * that.zx, this.zx * that.xz + this.zz * that.zz,
                this.xx * that.xt + this.xz * that.zt + this.xt,
                this.yt + that.yt,
                this.zx * that.xt + this.zz * that.zt + this.zt
            );
        }

        return before.andThen(this);
    }

    @Override
    public Transform compose(Transform before) {
        if(before instanceof CoarseTransform) {
            return compose((CoarseTransform) before);
        }

        return before.andThen(this);
    }

    @Override
    public CoarseTransform andThen(CoarseTransform after) {
        return after.compose(this);
    }

    @Override
    public Transform andThen(Transform after) {
        return after.compose(this);
    }

    @Override
    public BlockRotoflection orientation() {
        /*

        + 0     0 -     - 0     0 +
        0 +     + 0     0 -     - 0

        - 0     0 -     + 0     0 +
        0 +     - 0     0 -     + 0

         */
        if(xx < 0) {
            if(zz < 0) {
                return BlockRotoflection.of(false, 2);
            } else {
                return BlockRotoflection.of(true, 0);
            }
        } else if(xx > 0) {
            if(zz < 0) {
                return BlockRotoflection.of(true, 2);
            } else {
                return BlockRotoflection.of(false, 0);
            }
        } else if(xz < 0) {
            if(zx < 0) {
                return BlockRotoflection.of(true, 1);
            } else {
                return BlockRotoflection.of(false, 1);
            }
        } else {
            if(zx < 0) {
                return BlockRotoflection.of(false, 3);
            } else {
                return BlockRotoflection.of(true, 3);
            }
        }
    }

    @Override
    public int hashCode() {
        // Identity transform is always 0
        return isIdentity() ? 0 : Arrays.hashCode(new int[]{xx, xz, zx, zz, xt, yt, zt});
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(obj instanceof BlockTransform) {
            final BlockTransform that = (BlockTransform) obj;
            return this.xx == that.xx &&
                   this.xz == that.xz &&
                   this.zx == that.zx &&
                   this.zz == that.zz &&
                   this.xt == that.xt &&
                   this.yt == that.yt &&
                   this.zt == that.zt;
        }

        return obj.equals(this);
    }

    @Override
    public String toString() {
        return Transform.class.getSimpleName() +
               "{orientation=" + orientation() +
               " translation=(" + xt + ", " + yt + ", " + zt + ")}";
    }
}
