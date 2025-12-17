package de.shadowcrest.mod.util;

import de.shadowcrest.mod.ShadowCrestMod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class MessageUtil {

    private MessageUtil() {}

    // Für normale Chat-Nachrichten (String)
    public static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    // Für kick(...) etc. (Adventure Component)
    public static Component component(String legacyWithAmpersandColors) {
        String s = legacyWithAmpersandColors == null ? "" : legacyWithAmpersandColors;
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public static String raw(ShadowCrestMod plugin, String path) {
        return plugin.getConfig().getString(path, "");
    }

    public static String msg(ShadowCrestMod plugin, String path) {
        String prefix = raw(plugin, "prefix");
        return color(raw(plugin, path).replace("{prefix}", prefix));
    }

    public static Map<String, String> ph(Object... keyValue) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i + 1 < keyValue.length; i += 2) {
            m.put(String.valueOf(keyValue[i]), String.valueOf(keyValue[i + 1]));
        }
        return m;
    }

    public static String format(ShadowCrestMod plugin, String path, Map<String, String> placeholders) {
        String s = raw(plugin, path);
        s = s.replace("{prefix}", raw(plugin, "prefix"));
        for (var e : placeholders.entrySet()) {
            s = s.replace("{" + e.getKey() + "}", e.getValue());
        }
        return color(s);
    }

    // Wie format(...) aber als Component (für kick)
    public static Component formatComponent(ShadowCrestMod plugin, String path, Map<String, String> placeholders) {
        String raw = raw(plugin, path);
        raw = raw.replace("{prefix}", raw(plugin, "prefix"));
        for (var e : placeholders.entrySet()) {
            raw = raw.replace("{" + e.getKey() + "}", e.getValue());
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }

    public static void broadcastToStaff(String permission, String message) {
        String colored = color(message);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(permission)) {
                p.sendMessage(colored);
            }
        }
    }
}
