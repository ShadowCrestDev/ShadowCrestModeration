package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.data.PlayerData;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

import java.util.UUID;

public class ClearWarnsCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public ClearWarnsCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("shadowcrest.mod.clearwarns")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.clearwarns_usage"));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);

        if (off == null || off.getUniqueId() == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        UUID uuid = off.getUniqueId();
        PlayerData data = plugin.getDataManager().load(uuid);

        int oldWarns = data.getWarns();

        data.clearWarns();
        plugin.getDataManager().save(data);

        String staff = sender.getName();
        String staffMessage = MessageUtil.format(
                plugin,
                "messages.staff_action.clearwarns",
                MessageUtil.ph("staff", staff, "player", targetName, "oldwarns", oldWarns)
        );
        MessageUtil.broadcastToStaff("shadowcrest.mod.notify", staffMessage);

        sender.sendMessage(
                MessageUtil.format(
                        plugin,
                        "messages.clearwarns_done",
                        MessageUtil.ph("player", targetName)
                )
        );

        return true;
    }
}
