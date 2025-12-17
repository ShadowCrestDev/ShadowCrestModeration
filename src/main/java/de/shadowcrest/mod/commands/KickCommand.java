package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public KickCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("shadowcrest.mod.kick")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 2) {
            // optional besser: messages.kick_usage (sonst reason_required)
            String usage = MessageUtil.msg(plugin, "messages.kick_usage");
            sender.sendMessage(usage == null || usage.isBlank()
                    ? MessageUtil.msg(plugin, "messages.reason_required")
                    : usage);
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        String reason = joinArgs(args, 1);

        String staff = sender.getName();

        // Staff message einmal bauen
        String staffMessage = MessageUtil.format(
                plugin,
                "messages.staff_action.kick",
                MessageUtil.ph("staff", staff, "player", target.getName(), "reason", reason)
        );

        // Ingame Staff-Log
        MessageUtil.broadcastToStaff("shadowcrest.mod.notify", staffMessage);

        // Discord Webhook
        if (plugin.getConfig().getBoolean("discord.send.kick", true)) {
            de.shadowcrest.mod.util.DiscordNotifier.notify(plugin, staffMessage);
        }

        // Kick Screen als Component
        String screen = MessageUtil.format(
                plugin,
                "messages.kick_screen",
                MessageUtil.ph("reason", reason)
        );
        target.kick(MessageUtil.component(screen));

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
