package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class IpCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public IpCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // ❌ Nur Konsole erlaubt
        if (sender instanceof Player) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.console_only"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.ip_usage"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || target.getAddress() == null || target.getAddress().getAddress() == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        String ip = target.getAddress().getAddress().getHostAddress();

        sender.sendMessage(
                MessageUtil.format(
                        plugin,
                        "messages.ip_result",
                        MessageUtil.ph("player", target.getName(), "ip", ip)
                )
        );

        return true;
    }
}
