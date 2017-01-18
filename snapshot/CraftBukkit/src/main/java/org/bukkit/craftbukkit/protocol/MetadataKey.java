package org.bukkit.craftbukkit.protocol;

import java.util.OptionalInt;
import java.util.Set;
import java.util.function.UnaryOperator;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import io.netty.handler.codec.EncoderException;
import net.minecraft.server.DataWatcher;
import net.minecraft.server.DataWatcherObject;
import net.minecraft.server.DataWatcherRegistry;
import net.minecraft.server.DataWatcherSerializer;
import net.minecraft.server.Entity;
import net.minecraft.server.PacketDataSerializer;

/**
 * Replacement for DataWatcherObject that supports multiple protocol versions.
 * A single key can have different IDs for each protocol, or be completely absent
 * from some protocols. It can also have transforms applied for certain protocols.
 *
 * Note that ALL metadata uses this class.
 */
public class MetadataKey<T> extends DataWatcherObject<T> {

    // proto -> entity -> count
    private static final Table<Integer, Class<? extends Entity>, Integer> AUTO_IDS = HashBasedTable.create();
    private static final SetMultimap<Integer, Class<? extends Entity>> EXPLICIT_IDS = HashMultimap.create();

    private static int autoId(int proto, Class<? extends Entity> entity) {
        for(Class<? extends Entity> e : EXPLICIT_IDS.get(proto)) {
            if(e.isAssignableFrom(entity)) {
                throw new IllegalStateException(
                    "Cannot generate auto ID for entity " + entity.getSimpleName() +
                    " in protocol " + proto +
                    " after explicit ID has been defined for entity " + e.getSimpleName()
                );
            }
        }
        return autoIdNoCheck(proto, entity);
    }

    private static int autoIdNoCheck(int proto, Class<? extends Entity> entity) {
        for(Class<?> cls = entity; Entity.class.isAssignableFrom(cls); cls = cls.getSuperclass()) {
            final Integer count = AUTO_IDS.get(proto, cls);
            if(count != null) return count + 1;
        }
        return 0;
    }

    public static <T> MetadataKey<T> auto(Class<? extends Entity> entity, DataWatcherSerializer<T> type, Range<Integer> protos) {
        return auto(entity, type, Protocol.supported(protos));
    }

    public static <T> MetadataKey<T> auto(Class<? extends Entity> entity, DataWatcherSerializer<T> type, Set<Integer> protos) {
        final ImmutableTable.Builder<Integer, Class<? extends Entity>, Integer> ids = ImmutableTable.builder();
        for(Integer proto : protos) {
            final int id = autoId(proto, entity);
            AUTO_IDS.put(proto, entity, id);
            ids.put(proto, entity, id);
        }
        return new MetadataKey<>(type, ids.build(), ImmutableListMultimap.of());
    }

    private final Table<Integer, Class<? extends Entity>, Integer> ids;
    private final ListMultimap<Integer, UnaryOperator<T>> transforms;

    private MetadataKey(DataWatcherSerializer<T> type, Table<Integer, Class<? extends Entity>, Integer> ids, ListMultimap<Integer, UnaryOperator<T>> transforms) {
        super(-1, type);
        this.ids = ids;
        this.transforms = transforms;

        //log("MetadataKey");
        //ids.columnMap().forEach((entity, idmap) -> {
        //    log("  " + entity.getSimpleName());
        //    idmap.forEach((proto, id) -> {
        //        log("    " + proto + " -> " + id);
        //    });
        //});
    }

    private static void log(String s) {
        System.out.println(s);
    }

    @Override
    public boolean equals(Object that) {
        return this == that;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public int a() {
        throw new UnsupportedOperationException();
    }

    public OptionalInt id(int proto, Class<? extends Entity> entity) {
        for(Class<?> cls = entity; Entity.class.isAssignableFrom(cls); cls = cls.getSuperclass()) {
            final Integer id = ids.get(proto, cls);
            if(id != null) return OptionalInt.of(id);
        }
        return OptionalInt.empty();
    }

    public DataWatcherSerializer<T> type() {
        return b();
    }

    public int typeId() {
        final int typeId = DataWatcherRegistry.b(type());
        if (typeId < 0) {
            throw new EncoderException("Unknown serializer type " + type());
        }
        return typeId;
    }

    public void send(PacketDataSerializer serializer, DataWatcher.Item<T> item) {
        id(serializer.protocolVersion, item.entityClass).ifPresent(id -> {
            T value = item.value();
            for(UnaryOperator<T> transform : transforms.get(serializer.protocolVersion)) {
                value = transform.apply(value);
            }
            serializer.writeByte(id);
            serializer.writeVarInt(typeId());
            type().a(serializer, item.value());
        });
    }

    public static <T> Builder<T> builder(Class<? extends Entity> entity, DataWatcherSerializer<T> type) {
        return new Builder<>(entity, type);
    }

    public static class Builder<T> {

        private final Class<? extends Entity> entity;
        private final DataWatcherSerializer<T> type;
        private final ImmutableTable.Builder<Integer, Class<? extends Entity>, Integer> ids = ImmutableTable.builder();
        private final ImmutableListMultimap.Builder<Integer, UnaryOperator<T>> transforms = ImmutableListMultimap.builder();

        public Builder(Class<? extends Entity> entity, DataWatcherSerializer<T> type) {
            this.type = type;
            this.entity = entity;
        }

        public MetadataKey<T> build() {
            final ImmutableTable<Integer, Class<? extends Entity>, Integer> ids = this.ids.build();
            ids.rowMap().forEach((proto, column) -> {
                column.forEach((entity, id) -> {
                    final int autoId = autoIdNoCheck(proto, entity);
                    if(id < autoId) {
                        throw new IllegalStateException(
                            "Explicit ID " + id +
                            " is below last auto ID " + autoId +
                            " for entity " + entity.getSimpleName() +
                            " in protocol " + proto
                        );
                    }
                    EXPLICIT_IDS.put(proto, entity);
                });
            });
            return new MetadataKey<>(type, ids, transforms.build());
        }

        public Builder<T> id(int proto, Class<? extends Entity> entity, int id) {
            ids.put(proto, entity, id);
            return this;
        }

        public Builder<T> id(int proto, int id) {
            return id(proto, entity, id);
        }

        public Builder<T> id(Set<Integer> protos, Class<? extends Entity> entity, int id) {
            protos.forEach(proto -> id(proto, entity, id));
            return this;
        }

        public Builder<T> id(Set<Integer> protos, int id) {
            return id(protos, entity, id);
        }

        public Builder<T> id(Range<Integer> protos, Class<? extends Entity> entity, int id) {
            return id(Protocol.supported(protos), entity, id);
        }

        public Builder<T> id(Range<Integer> protos, int id) {
            return id(protos, entity, id);
        }

        public Builder<T> transform(int proto, UnaryOperator<T> transform) {
            transforms.put(proto, transform);
            return this;
        }

        public Builder<T> transform(Set<Integer> protos, UnaryOperator<T> transform) {
            protos.forEach(proto -> transform(proto, transform));
            return this;
        }

        public Builder<T> transform(Range<Integer> protos, UnaryOperator<T> transform) {
            return transform(Protocol.supported(protos), transform);
        }
    }
}
