package de.shadowcrest.mod.util;

import org.bukkit.ChatColor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public final class DiscordWebhook {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private DiscordWebhook() {}

    public static void send(String webhookUrl, String username, String content) {
        if (webhookUrl == null || webhookUrl.isBlank()) return;

        // Farben entfernen (Discord kann kein Minecraft-ChatColor)
        String clean = ChatColor.stripColor(MessageUtil.color(content));

        // sehr simples JSON (kein Embed, reicht für Logs)
        String json = "{"
                + "\"username\":\"" + escape(username == null ? "ShadowCrest" : username) + "\","
                + "\"content\":\"" + escape(clean) + "\""
                + "}";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        CLIENT.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
