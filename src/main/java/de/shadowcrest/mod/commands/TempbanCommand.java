package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.data.TimeUtil;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Date;

public class TempbanCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public TempbanCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadowcrest.mod.tempban")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 3) {
            // optional besser: messages.tempban_usage (sonst duration_required)
            String usage = MessageUtil.msg(plugin, "messages.tempban_usage");
            sender.sendMessage(usage == null || usage.isBlank()
                    ? MessageUtil.msg(plugin, "messages.duration_required")
                    : usage);
            return true;
        }

        String targetName = args[0];
        String durationStr = args[1];
        String reason = joinArgs(args, 2);

        if (reason.isBlank()) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.reason_required"));
            return true;
        }

        long ms = TimeUtil.parseDurationToMillis(durationStr);
        if (ms <= 0) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.invalid_duration"));
            return true;
        }

        Date until = new Date(System.currentTimeMillis() + ms);

        Player online = Bukkit.getPlayerExact(targetName);

        // Tempban immer nach Namen erlauben (auch wenn Spieler nie online war)
        Bukkit.getBanList(BanList.Type.NAME).addBan(targetName, reason, until, "ShadowCrest");

        String staff = sender.getName();

        // Staff message einmal bauen
        String staffMessage = MessageUtil.format(
                plugin,
                "messages.staff_action.tempban",
                MessageUtil.ph("staff", staff, "player", targetName, "duration", durationStr, "reason", reason)
        );

        // Ingame Staff-Log
        MessageUtil.broadcastToStaff("shadowcrest.mod.notify", staffMessage);

        // Discord Webhook
        if (plugin.getConfig().getBoolean("discord.send.tempban", true)) {
            de.shadowcrest.mod.util.DiscordNotifier.notify(plugin, staffMessage);
        }

        if (online != null) {
            String screen = MessageUtil.format(
                    plugin,
                    "messages.tempban_screen",
                    MessageUtil.ph("duration", durationStr, "reason", reason)
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
