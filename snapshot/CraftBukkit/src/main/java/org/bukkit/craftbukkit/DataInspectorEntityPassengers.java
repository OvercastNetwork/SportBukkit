package org.bukkit.craftbukkit;

import net.minecraft.server.DataConverter;
import net.minecraft.server.DataConverterTypes;
import net.minecraft.server.DataInspector;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;

/**
 * Run data converters on passenger entities
 */
public class DataInspectorEntityPassengers implements DataInspector {
    @Override
    public NBTTagCompound a(DataConverter converter, NBTTagCompound nbt, int version) {
        if(nbt.hasKeyOfType("Passengers", 9)) {
            final NBTTagList passengers = nbt.getList("Passengers", 10);
            for(int i = 0; i < passengers.size(); i++) {
                passengers.a(i, converter.a(DataConverterTypes.ENTITY, passengers.get(i), version));
            }
        }
        return nbt;
    }
}
