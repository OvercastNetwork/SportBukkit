package org.bukkit.plugin;

import java.time.Duration;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tc.oc.minecraft.api.scheduler.Scheduler;

class PluginScheduler implements Scheduler {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    @Inject PluginScheduler(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    private long ticks(Duration duration) {
        return Math.max(1, scheduler.toTicks(duration, 1));
    }

    @Override
    public BukkitTask schedule(boolean sync, @Nullable Duration delay, @Nullable Duration period, Runnable task) {
        final long delayTicks = delay == null ? 0 : ticks(delay);
        final long periodTicks = period == null ? -1 : ticks(period);

        return sync ? scheduler.runTaskTimer(plugin, task, delayTicks, periodTicks)
                    : scheduler.runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }
}
