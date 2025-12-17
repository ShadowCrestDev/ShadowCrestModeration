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
            // optional besser: messages.ban_usage (sonst reason_required)
            String usage = MessageUtil.msg(plugin, "messages.ban_usage");
            sender.sendMessage(usage == null || usage.isBlank()
                    ? MessageUtil.msg(plugin, "messages.reason_required")
                    : usage);
            return true;
        }

        String targetName = args[0];
        String reason = joinArgs(args, 1);

        Player online = Bukkit.getPlayerExact(targetName);

        // Ban immer nach Namen erlauben (auch wenn Spieler nie online war)
        Bukkit.getBanList(BanList.Type.NAME).addBan(targetName, reason, null, "ShadowCrest");

        String staff = sender.getName();

        // Staff message einmal bauen
        String staffMessage = MessageUtil.format(
                plugin,
                "messages.staff_action.ban",
                MessageUtil.ph("staff", staff, "player", targetName, "reason", reason)
        );

        // Ingame Staff-Log
        MessageUtil.broadcastToStaff("shadowcrest.mod.notify", staffMessage);

        // Discord Webhook
        if (plugin.getConfig().getBoolean("discord.send.ban", true)) {
            de.shadowcrest.mod.util.DiscordNotifier.notify(plugin, staffMessage);
        }

        // Wenn online -> kicken
        if (online != null) {
            String screen = MessageUtil.format(plugin, "messages.ban_screen", MessageUtil.ph("reason", reason));
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
