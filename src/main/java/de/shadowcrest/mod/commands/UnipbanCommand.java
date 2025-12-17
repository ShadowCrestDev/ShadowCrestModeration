package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class UnipbanCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    // IPv4 Validator
    private static final Pattern IPV4 =
            Pattern.compile("^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$");

    public UnipbanCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadowcrest.mod.unipban")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.unipban_usage"));
            return true;
        }

        String ip = args[0];

        if (!IPV4.matcher(ip).matches()) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.invalid_ip"));
            return true;
        }

        BanList banList = Bukkit.getBanList(BanList.Type.IP);

        if (banList.getBanEntry(ip) == null) {
            sender.sendMessage(MessageUtil.format(
                    plugin,
                    "messages.unipban_not_banned",
                    MessageUtil.ph("ip", ip)
            ));
            return true;
        }

        banList.pardon(ip);

        String staff = sender.getName();

        // Staff message einmal bauen
        String staffMessage = MessageUtil.format(
                plugin,
                "messages.staff_action.unipban",
                MessageUtil.ph("staff", staff, "ip", ip)
        );

        // Ingame Staff-Log
        MessageUtil.broadcastToStaff("shadowcrest.mod.notify", staffMessage);

        // Discord Webhook
        if (plugin.getConfig().getBoolean("discord.send.unipban", true)) {
            de.shadowcrest.mod.util.DiscordNotifier.notify(plugin, staffMessage);
        }

        // Sender confirmation
        sender.sendMessage(MessageUtil.format(
                plugin,
                "messages.unipban_done",
                MessageUtil.ph("ip", ip)
        ));

        return true;
    }
}
