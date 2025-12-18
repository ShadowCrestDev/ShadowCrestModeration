package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class IpbanCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    private static final Pattern IPV4 =
            Pattern.compile("^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$");

    public IpbanCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadowcrest.mod.ipban")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.reason_required"));
            return true;
        }

        String targetOrIp = args[0];
        String reason = joinArgs(args, 1);

        String ipToBan;
        String displayTarget;

        Player online = Bukkit.getPlayerExact(targetOrIp);
        if (online != null && online.getAddress() != null && online.getAddress().getAddress() != null) {
            ipToBan = online.getAddress().getAddress().getHostAddress();
            displayTarget = online.getName();
        } else {
            if (!IPV4.matcher(targetOrIp).matches()) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.invalid_ip"));
                return true;
            }
            ipToBan = targetOrIp;
            displayTarget = targetOrIp;
        }

        Bukkit.getBanList(BanList.Type.IP).addBan(ipToBan, reason, null, "ShadowCrest");

        String staff = sender.getName();
        String staffMessage = MessageUtil.format(
                plugin,
                "messages.staff_action.ipban",
                MessageUtil.ph("staff", staff, "player", displayTarget, "reason", reason)
        );
        MessageUtil.broadcastToStaff("shadowcrest.mod.notify", staffMessage);

        if (online != null) {
            String screen = MessageUtil.format(
                    plugin,
                    "messages.ipban_screen",
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
