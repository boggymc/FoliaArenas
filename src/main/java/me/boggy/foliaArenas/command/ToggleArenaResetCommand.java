package me.boggy.foliaArenas.command;

import me.boggy.foliaArenas.FoliaArenas;
import me.boggy.foliaArenas.manager.SchedulerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleArenaResetCommand implements CommandExecutor {
    private final FoliaArenas plugin;

    public ToggleArenaResetCommand(FoliaArenas plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(plugin.getConfig().getString("toggle-permission"))) {
            return false;
        }
        if (plugin.getArenaManager().shouldReset()) {
            plugin.getArenaManager().setShouldReset(false);
            plugin.getTasks().forEach(SchedulerManager.Task::cancel);
            plugin.getTasks().clear();

            // TODO: RESET ALL BLOCKS HERE

            if (sender instanceof Player) {
                sender.sendMessage("Arena resets are now " + ChatColor.RED + "off.");
            }
        } else {
            plugin.getArenaManager().setShouldReset(true);
            if (sender instanceof Player) {
                sender.sendMessage("Arena resets are now " + ChatColor.GREEN + "on.");
            }
        }

        return true;
    }

}
