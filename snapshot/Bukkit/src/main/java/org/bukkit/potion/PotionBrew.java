package org.bukkit.potion;

import java.util.List;

import org.bukkit.registry.Registerable;

public interface PotionBrew extends Registerable {
    List<PotionEffect> effects();
}
