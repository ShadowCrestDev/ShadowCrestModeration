package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Map;

public class VanishCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public VanishCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("shadowcrest.mod.vanish")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        // /vanish (self)
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.only_players"));
                return true;
            }

            boolean enabled = plugin.getVanishManager().toggle(p);
            p.sendMessage(MessageUtil.msg(plugin, enabled
                    ? "messages.vanish_enabled"
                    : "messages.vanish_disabled"));
            return true;
        }

        // /vanish <player>
        OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
        if (off == null || off.getName() == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        if (!(off.isOnline()) || off.getPlayer() == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        Player target = off.getPlayer();
        boolean enabled = plugin.getVanishManager().toggle(target);

        sender.sendMessage(plugin.getLang().get(
                enabled ? "messages.vanish_enabled_other" : "messages.vanish_disabled_other",
                Map.of("player", target.getName())
        ));

        target.sendMessage(MessageUtil.msg(plugin, enabled
                ? "messages.vanish_enabled"
                : "messages.vanish_disabled"));

        return true;
    }
}
