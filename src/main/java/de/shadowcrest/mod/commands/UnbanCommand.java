package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

public class UnbanCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public UnbanCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadowcrest.mod.unban")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.unban_usage"));
            return true;
        }

        String targetName = args[0];
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);

        if (banList.getBanEntry(targetName) == null) {
            sender.sendMessage(MessageUtil.format(
                    plugin,
                    "messages.unban_not_banned",
                    MessageUtil.ph("player", targetName)
            ));
            return true;
        }

        banList.pardon(targetName);

        String staff = sender.getName();

        // Staff notify (Minecraft)
        String staffMessage = MessageUtil.format(
                plugin,
                "messages.staff_action.unban",
                MessageUtil.ph("staff", staff, "player", targetName)
        );
        MessageUtil.broadcastToStaff("shadowcrest.mod.notify", staffMessage);

        // ✅ Discord webhook
        if (plugin.getModNotifier() != null) {
            plugin.getModNotifier().unban(staff, targetName);
        }

        sender.sendMessage(MessageUtil.format(
                plugin,
                "messages.unban_done",
                MessageUtil.ph("player", targetName)
        ));

        return true;
    }
}
