package me.boggy.foliaArenas.manager;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.boggy.foliaArenas.FoliaArenas;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

public final class SchedulerManager {

    private static FoliaArenas plugin;
    private static boolean isFolia;

    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;

        } catch (final ClassNotFoundException e) {
            isFolia = false;
        }
    }

    public SchedulerManager(FoliaArenas plugin) {
        SchedulerManager.plugin = plugin;
    }

    public static void run(Runnable runnable) {
        if (isFolia)
            Bukkit.getGlobalRegionScheduler()
                    .execute(plugin, runnable);

        else
            Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static Task runLater(Runnable runnable, long delayTicks) {
        if (isFolia)
            return new Task(Bukkit.getGlobalRegionScheduler()
                    .runDelayed(plugin, t -> runnable.run(), delayTicks));

        else
            return new Task(Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks));
    }

    public static Task runLaterRegion(Runnable runnable, long delayTicks, Location location) {
        if (isFolia)
            return new Task(Bukkit.getRegionScheduler()
                    .runDelayed(plugin, location, t -> runnable.run(), delayTicks));

        else
            return new Task(Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks));
    }

    public static Task runTimer(Runnable runnable, long delayTicks, long periodTicks) {
        if (isFolia)
            return new Task(Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(plugin, t -> runnable.run(), delayTicks < 1 ? 1 : delayTicks, periodTicks));

        else
            return new Task(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks));
    }

    public static boolean isFolia() {
        return isFolia;
    }

    public static class Task {

        private Object foliaTask;
        private BukkitTask bukkitTask;

        Task(Object foliaTask) {
            this.foliaTask = foliaTask;
        }

        Task(BukkitTask bukkitTask) {
            this.bukkitTask = bukkitTask;
        }

        public void cancel() {
            if (foliaTask != null)
                ((ScheduledTask) foliaTask).cancel();
            else
                bukkitTask.cancel();
        }
    }
}