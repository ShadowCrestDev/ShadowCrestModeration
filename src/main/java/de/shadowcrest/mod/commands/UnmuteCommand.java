package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

import java.util.Map;

public class UnmuteCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public UnmuteCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("shadowcrest.mod.unmute")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.unmute_usage"));
            return true;
        }

        OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
        boolean unknown = (off == null || (!off.hasPlayedBefore() && off.getName() == null));
        if (unknown || off.getUniqueId() == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        String playerName = (off.getName() == null ? args[0] : off.getName());
        String staff = sender.getName();

        boolean removed = plugin.getMuteManager().unmute(off.getUniqueId());
        if (!removed) {
            sender.sendMessage(plugin.getLang().get("messages.unmute_not_muted",
                    Map.of("player", playerName)));
            return true;
        }

        // ✅ Discord webhook
        if (plugin.getModNotifier() != null) {
            plugin.getModNotifier().unmute(staff, playerName);
        }

        sender.sendMessage(plugin.getLang().get("messages.unmute_done",
                Map.of("player", playerName)));

        if (off.isOnline() && off.getPlayer() != null) {
            off.getPlayer().sendMessage(MessageUtil.msg(plugin, "messages.unmuted_you"));
        }

        return true;
    }
}
