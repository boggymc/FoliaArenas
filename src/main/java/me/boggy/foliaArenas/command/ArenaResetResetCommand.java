package me.boggy.foliaArenas.command;

import me.boggy.foliaArenas.FoliaArenas;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaResetResetCommand implements CommandExecutor {
    private final FoliaArenas plugin;

    public ArenaResetResetCommand(FoliaArenas plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission(plugin.getConfig().getString("reset-permission"))) {
            return false;
        }

        plugin.getArenaManager().startRegionReset();

        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.GREEN + "Arena is being reset");
        }

        return true;
    }
}
