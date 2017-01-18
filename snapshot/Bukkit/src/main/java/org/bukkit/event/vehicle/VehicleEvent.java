package org.bukkit.event.vehicle;

import org.bukkit.World;
import org.bukkit.Physical;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;

/**
 * Represents a vehicle-related event.
 */
public abstract class VehicleEvent extends Event implements Physical {
    protected Vehicle vehicle;

    public VehicleEvent(final Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Get the vehicle.
     *
     * @return the vehicle
     */
    public final Vehicle getVehicle() {
        return vehicle;
    }

    @Override
    public World getWorld() {
        return getVehicle().getWorld();
    }
}
