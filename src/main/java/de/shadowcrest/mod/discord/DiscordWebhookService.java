package de.shadowcrest.mod.discord;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class DiscordWebhookService {

    private final ShadowCrestMod plugin;
    private final HttpClient http = HttpClient.newHttpClient();

    public DiscordWebhookService(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("discord.enabled", false);
    }

    private String getWebhookUrl() {
        return plugin.getConfig().getString("discord.webhook_url", "");
    }

    private String getUsername() {
        return plugin.getConfig().getString("discord.username", "ShadowCrestModeration");
    }

    private String getAvatarUrl() {
        return plugin.getConfig().getString("discord.avatar_url", "");
    }
    private String stripColors(String s) {
        if (s == null) return "";
        return s.replaceAll("(?i)§[0-9A-FK-OR]", "")
                .replaceAll("(?i)&[0-9A-FK-OR]", "");
    }


    private static String jsonEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    public void sendPlainAsync(String content) {
        if (!isEnabled()) return;

        String url = getWebhookUrl();
        if (url == null || url.isBlank()) return;

        // 🔥 HIER Farben entfernen
        String cleanContent = stripColors(content);

        // run async to prevent lag
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String username = jsonEscape(getUsername());
                String avatar = jsonEscape(getAvatarUrl());
                String msg = jsonEscape(cleanContent); // <- clean text

                StringBuilder body = new StringBuilder();
                body.append("{");
                body.append("\"content\":\"").append(msg).append("\",");
                body.append("\"username\":\"").append(username).append("\"");

                if (avatar != null && !avatar.isBlank()) {
                    body.append(",\"avatar_url\":\"").append(avatar).append("\"");
                }

                body.append("}");

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                        .build();

                http.send(req, HttpResponse.BodyHandlers.discarding());
            } catch (Exception ex) {
                plugin.getLogger().warning("Discord webhook send failed: " + ex.getMessage());
            }
        });
    }
}
