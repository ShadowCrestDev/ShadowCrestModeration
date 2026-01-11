package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public BanCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadowcrest.mod.ban")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.reason_required"));
            return true;
        }

        String targetName = args[0];
        String reason = joinArgs(args, 1);

        Player online = Bukkit.getPlayerExact(targetName);

        Bukkit.getBanList(BanList.Type.NAME).addBan(targetName, reason, null, "ShadowCrest");

        String staff = sender.getName();

        // Staff broadcast
        String staffMessage = MessageUtil.format(
                plugin,
                "messages.staff_action.ban",
                MessageUtil.ph("staff", staff, "player", targetName, "reason", reason)
        );
        MessageUtil.broadcastToStaff("shadowcrest.mod.notify", staffMessage);

        // ✅ Discord webhook
        if (plugin.getModNotifier() != null) {
            plugin.getModNotifier().ban(staff, targetName, reason);
        }

        // Kick if online
        if (online != null) {
            String screen = MessageUtil.format(
                    plugin,
                    "messages.ban_screen",
                    MessageUtil.ph("reason", reason)
            );
            online.kick(MessageUtil.component(screen));
        }

        return true;
    }

    private String joinArgs(String[] args, int from) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < args.length; i++) {
            sb.append(args[i]);
            if (i + 1 < args.length) sb.append(" ");
        }
        return sb.toString();
    }
}
