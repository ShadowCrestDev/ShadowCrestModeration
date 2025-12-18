package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.data.PlaytimeUtil;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

public class PlaytimeCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public PlaytimeCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!plugin.getConfig().getBoolean("playtime.enabled", true)) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.playtime_disabled"));
            return true;
        }

        if (!sender.hasPermission("shadowcrest.mod.playtime")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.playtime_usage"));
            return true;
        }

        String name = args[0];
        OfflinePlayer off = Bukkit.getOfflinePlayer(name);

        if (off == null || (off.getName() == null && !off.hasPlayedBefore())) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        long ticks = PlaytimeUtil.getPlayTicks(off);
        String fmt = plugin.getConfig().getString("playtime.format", "{days}d {hours}h {minutes}m");
        String playtime = PlaytimeUtil.formatPlaytime(ticks, fmt);

        sender.sendMessage(MessageUtil.format(
                plugin,
                "messages.playtime_result",
                MessageUtil.ph("player", name, "playtime", playtime)
        ));

        return true;
    }
}
