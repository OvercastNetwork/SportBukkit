package org.bukkit.attribute;

import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.EquipmentSlot;

/**
 * An {@link AttributeModifier} attached to an item, with an optional slot.
 */
public class ItemAttributeModifier implements ConfigurationSerializable {

    private final EquipmentSlot slot;
    private final AttributeModifier modifier;

    public ItemAttributeModifier(EquipmentSlot slot, AttributeModifier modifier) {
        this.modifier = modifier;
        this.slot = slot;
    }

    /**
     * The slot the item must be in for te modifier to take effect,
     * or null if the slot doesn't matter.
     */
    public EquipmentSlot getSlot() {
        return slot;
    }

    public AttributeModifier getModifier() {
        return modifier;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = modifier.serialize();
        map.put("slot", serializeSlot(slot));
        return map;
    }
    public static ItemAttributeModifier deserialize(Map<String, Object> args) {
        return new ItemAttributeModifier(deserializeSlot((String) args.get("slot")), AttributeModifier.deserialize(args));
    }

    public static String serializeSlot(EquipmentSlot slot) {
        if(slot == null) return null;
        return slotNames[slot.ordinal()];
    }

    public static EquipmentSlot deserializeSlot(String slot) {
        if(slot == null) return null;
        for(int i = 0; i < slotNames.length; i++) {
            if(slot.equals(slotNames[i])) return EquipmentSlot.values()[i];
        }
        return null;
    }

    private static final String[] slotNames = new String[EquipmentSlot.values().length];
    static {
        slotNames[EquipmentSlot.HAND.ordinal()] = "mainhand";
        slotNames[EquipmentSlot.OFF_HAND.ordinal()] = "offhand";
        slotNames[EquipmentSlot.HEAD.ordinal()] = "head";
        slotNames[EquipmentSlot.CHEST.ordinal()] = "chest";
        slotNames[EquipmentSlot.LEGS.ordinal()] = "legs";
        slotNames[EquipmentSlot.FEET.ordinal()] = "feet";
    }
}
