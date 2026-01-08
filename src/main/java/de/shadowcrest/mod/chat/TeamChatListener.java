package de.shadowcrest.mod.chat;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

public class TeamChatListener implements Listener {

    private final ShadowCrestMod plugin;
    private final TeamChatManager teamChat;

    public TeamChatListener(ShadowCrestMod plugin, TeamChatManager teamChat) {
        this.plugin = plugin;
        this.teamChat = teamChat;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (!teamChat.isEnabled(p.getUniqueId())) return;
        if (!p.hasPermission("shadowcrest.mod.teamchat")) {
            teamChat.clear(p.getUniqueId());
            return;
        }

        e.setCancelled(true);

        String msg = e.getMessage().trim();
        if (msg.isBlank()) return;

        String format = plugin.getLang().get("messages.teamchat.format",
                Map.of("player", p.getName(), "message", msg));

        Bukkit.getOnlinePlayers().stream()
                .filter(pl -> pl.hasPermission("shadowcrest.mod.teamchat"))
                .forEach(pl -> pl.sendMessage(format));
    }
}
