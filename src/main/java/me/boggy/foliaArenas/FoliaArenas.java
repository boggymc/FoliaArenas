package me.boggy.foliaArenas;

import me.boggy.foliaArenas.command.ToggleArenaResetCommand;
import me.boggy.foliaArenas.command.ArenaResetResetCommand;
import me.boggy.foliaArenas.command.ArenaSaveCommand;
import me.boggy.foliaArenas.manager.ArenaManager;
import me.boggy.foliaArenas.manager.SchedulerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class FoliaArenas extends JavaPlugin {
    private ArenaManager arenaManager;

    private List<SchedulerManager.Task> tasks = new ArrayList<>();
    @Override
    public void onEnable() {
        saveDefaultConfig();
        new SchedulerManager(this);
        arenaManager = new ArenaManager(this);
        arenaManager.addArenas();
        Bukkit.getAsyncScheduler().runNow(this, (runnable) -> {
            arenaManager.loadRegion();

            int regenDelay = getConfig().getInt("regen-delay") * 20;

            Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, (runnable2) -> {
                if (arenaManager.shouldReset()) {
                    arenaManager.startRegionReset();
                }
            }, regenDelay, regenDelay);
        });
        getCommand("arenareset").setExecutor(new ToggleArenaResetCommand(this));
        getCommand("arenasave").setExecutor(new ArenaSaveCommand(this));
        getCommand("togglearenareset").setExecutor(new ArenaResetResetCommand(this));

    }

    public ArenaManager getArenaManager() { return arenaManager; }

    public List<SchedulerManager.Task> getTasks() { return tasks; }
}
