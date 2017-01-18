package org.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public enum PoseFlag {
    /** @see Entity#isOnGround() */
    GROUNDED,

    /** @see Entity#isDead() */
    DEAD,

    /** @see HumanEntity#isSleeping() */
    SLEEPING,

    /** @see Player#isSneaking() */
    SNEAKING,

    /** @see Player#isSprinting() */
    SPRINTING,

    /** @see HumanEntity#isBlocking() */
    BLOCKING,

    /** @see Player#isDigging() */
    DIGGING,

    /** @see LivingEntity#isGliding() */
    GLIDING,

    /** @see Player#isFlying() */
    FLYING,

    /** @see LivingEntity#isLeashed() */
    LEASHED,

    /** @see Entity#isInsideVehicle() */
    RIDING;
}
