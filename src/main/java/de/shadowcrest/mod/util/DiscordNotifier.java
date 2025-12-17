package de.shadowcrest.mod.util;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.Bukkit;

public final class DiscordNotifier {

    private DiscordNotifier() {}

    public static void notify(ShadowCrestMod plugin, String message) {
        if (!plugin.getConfig().getBoolean("discord.enabled", false)) return;

        String url = plugin.getConfig().getString("discord.webhook_url", "");
        String username = plugin.getConfig().getString("discord.username", "ShadowCrest");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DiscordWebhook.send(url, username, message);
        });
    }
}
