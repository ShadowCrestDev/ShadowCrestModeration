package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.chat.TeamChatManager;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Map;

public class TeamChatCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;
    private final TeamChatManager teamChat;

    public TeamChatCommand(ShadowCrestMod plugin, TeamChatManager teamChat) {
        this.plugin = plugin;
        this.teamChat = teamChat;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.only_players"));
            return true;
        }

        if (!p.hasPermission("shadowcrest.mod.teamchat")) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        // /teamchat  -> toggle
        if (args.length == 0) {
            boolean enabled = teamChat.toggle(p.getUniqueId());
            p.sendMessage(MessageUtil.msg(plugin, enabled
                    ? "messages.teamchat.enabled"
                    : "messages.teamchat.disabled"));
            return true;
        }

        // /teamchat <msg...>  -> one-time send
        String msg = String.join(" ", args).trim();
        if (msg.isBlank()) return true;

        String format = plugin.getLang().get("messages.teamchat.format",
                Map.of("player", p.getName(), "message", msg));

        Bukkit.getOnlinePlayers().stream()
                .filter(pl -> pl.hasPermission("shadowcrest.mod.teamchat"))
                .forEach(pl -> pl.sendMessage(format));

        return true;
    }
}
