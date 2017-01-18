package org.bukkit.block;

import java.util.Random;

import net.minecraft.server.BlockPosition;
import org.bukkit.geometry.Axis;
import org.bukkit.geometry.BlockRotoflection;
import org.bukkit.geometry.BlockReflection;
import org.bukkit.geometry.BlockRotation;
import org.bukkit.geometry.CoarseTransform;
import org.bukkit.geometry.Transform;
import org.bukkit.geometry.Vec3;
import org.bukkit.support.BukkitRuntimeTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransformTest extends BukkitRuntimeTest {

    Random random = new Random();

    Vec3 rand() {
        return new BlockPosition(random.nextInt(2000) - 1000,
                                 random.nextInt(2000) - 1000,
                                 random.nextInt(2000) - 1000);
    }

    @Test
    public void identity() throws Exception {
        Vec3 p = rand();
        assertEquals(p, Transform.identity().apply(p));
    }

    @Test
    public void inverseIdentity() throws Exception {
        Vec3 p = rand();
        assertEquals(Transform.identity(), Transform.identity().inverse());
        assertEquals(p, Transform.identity().inverse().apply(p));
    }

    @Test
    public void compoundIdentity() throws Exception {
        Vec3 p = rand();
        assertEquals(Transform.identity(), Transform.identity().andThen(Transform.identity()));
        assertEquals(Transform.identity(), Transform.identity().compose(Transform.identity()));
        assertEquals(p, Transform.identity().andThen(Transform.identity()).apply(p));
        assertEquals(p, Transform.identity().compose(Transform.identity()).apply(p));
    }

    @Test
    public void translation() throws Exception {
        Vec3 p = rand();
        Vec3 t = rand();
        assertEquals(p.plus(t),
                     CoarseTransform.translation(t).apply(p));
    }

    @Test
    public void inverseTranslation() throws Exception {
        Vec3 p = rand();
        Vec3 t = rand();
        assertEquals(p.plus(t.negate()),
                     CoarseTransform.translation(t).inverse().apply(p));
    }

    @Test
    public void compoundTranslation() throws Exception {
        Vec3 p = rand();
        Vec3 t1 = rand();
        Vec3 t2 = rand();

        assertEquals(CoarseTransform.translation(t1.plus(t2)),
                     CoarseTransform.translation(t1).andThen(CoarseTransform.translation(t2)));

        assertEquals(p.plus(t1).plus(t2),
                     CoarseTransform.translation(t1).andThen(CoarseTransform.translation(t2)).apply(p));

        assertEquals(CoarseTransform.translation(t1.plus(t2)),
                     CoarseTransform.translation(t1).compose(CoarseTransform.translation(t2)));
    }

    @Test
    public void rotation() throws Exception {
        Vec3 p = rand();
        Vec3 q;

        q = CoarseTransform.rotation(0).apply(p);
        assertEquals(p, q);

        q = CoarseTransform.rotation(1).apply(p);
        assertEquals(p.coarseX(), q.coarseZ());
        assertEquals(p.coarseY(), q.coarseY());
        assertEquals(p.coarseZ(), -q.coarseX());

        q = CoarseTransform.rotation(2).apply(p);
        assertEquals(p.coarseX(), -q.coarseX());
        assertEquals(p.coarseY(), q.coarseY());
        assertEquals(p.coarseZ(), -q.coarseZ());

        q = CoarseTransform.rotation(3).apply(p);
        assertEquals(p.coarseX(), -q.coarseZ());
        assertEquals(p.coarseY(), q.coarseY());
        assertEquals(p.coarseZ(), q.coarseX());
    }

    @Test
    public void negativeRotation() throws Exception {
        Vec3 p = rand();

        assertEquals(CoarseTransform.rotation(1).apply(p),
                     CoarseTransform.rotation(1 - 4).apply(p));
    }

    @Test
    public void wrappedRotation() throws Exception {
        Vec3 p = rand();

        assertEquals(CoarseTransform.rotation(1).apply(p),
                     CoarseTransform.rotation(1 + 4).apply(p));
    }

    @Test
    public void inverseRotation() throws Exception {
        Vec3 p = rand();

        assertEquals(CoarseTransform.rotation(-1).apply(p),
                     CoarseTransform.rotation(1).inverse().apply(p));
    }

    @Test
    public void compoundRotation() throws Exception {
        Vec3 p = rand();

        assertEquals(CoarseTransform.rotation(2).apply(p),
                     CoarseTransform.rotation(1).andThen(CoarseTransform.rotation(1)).apply(p));

        assertEquals(CoarseTransform.rotation(2).apply(p),
                     CoarseTransform.rotation(1).compose(CoarseTransform.rotation(1)).apply(p));
    }

    @Test
    public void compoundTransform() throws Exception {
        Vec3 p = rand();
        Transform t = CoarseTransform.translation(rand());
        Transform r = CoarseTransform.rotation(random.nextInt(4));

        assertEquals(t.apply(r.apply(p)),
                     t.compose(r).apply(p));

        assertEquals(r.apply(t.apply(p)),
                     r.compose(t).apply(p));

        assertEquals(r.apply(t.apply(p)),
                     t.andThen(r).apply(p));

        assertEquals(t.apply(r.apply(p)),
                     r.andThen(t).apply(p));
    }

    @Test
    public void manyTransforms() throws Exception {
        Vec3 p = rand();
        Vec3 q = p;
        Transform a = Transform.identity();
        Transform b = Transform.identity();
        Transform z;

        for(int i = 0; i < 10; i++) {
            z = i % 2 == 0 ? CoarseTransform.translation(rand())
                           : CoarseTransform.rotation(random.nextInt(4));

            q = z.apply(q);
            a = z.compose(a);
            b = b.andThen(z);

            assertEquals(q, a.apply(p));
            assertEquals(q, b.apply(p));
        }
    }

    @Test
    public void detectIdentityOrientation() throws Exception {
        assertEquals(BlockRotoflection.identity(), Transform.identity().orientation());
    }

    @Test
    public void detectReflection() throws Exception {
        assertEquals(BlockRotoflection.of(BlockReflection.X),
                     CoarseTransform.reflection(Axis.X).orientation());

        assertEquals(BlockRotoflection.of(BlockReflection.X, BlockRotation.CLOCKWISE_180),
                     CoarseTransform.reflection(Axis.Z).orientation());

        assertEquals(BlockRotoflection.of(BlockReflection.NONE, BlockRotation.CLOCKWISE_180),
                     CoarseTransform.reflection(Axis.X).andThen(CoarseTransform.reflection(Axis.Z)).orientation());
    }

    @Test
    public void detectRotation() throws Exception {
        assertEquals(BlockRotoflection.of(BlockRotation.CLOCKWISE_90), CoarseTransform.rotation(1).orientation());
        assertEquals(BlockRotoflection.of(BlockRotation.CLOCKWISE_180), CoarseTransform.rotation(2).orientation());
        assertEquals(BlockRotoflection.of(BlockRotation.CLOCKWISE_270), CoarseTransform.rotation(3).orientation());

        assertEquals(BlockRotoflection.of(BlockRotation.COUNTERCLOCKWISE_90), CoarseTransform.rotation(-1).orientation());
        assertEquals(BlockRotoflection.of(BlockRotation.COUNTERCLOCKWISE_180), CoarseTransform.rotation(-2).orientation());
        assertEquals(BlockRotoflection.of(BlockRotation.COUNTERCLOCKWISE_270), CoarseTransform.rotation(-3).orientation());
    }

    @Test
    public void detectReflectionAndRotation() throws Exception {
        assertEquals(BlockRotoflection.of(BlockReflection.X, BlockRotation.CLOCKWISE_90),
                     CoarseTransform.reflection(Axis.X).andThen(CoarseTransform.rotation(1)).orientation());

        assertEquals(BlockRotoflection.of(BlockReflection.Z, BlockRotation.COUNTERCLOCKWISE_90),
                     CoarseTransform.reflection(Axis.X).andThen(CoarseTransform.rotation(1)).orientation());
    }
}
