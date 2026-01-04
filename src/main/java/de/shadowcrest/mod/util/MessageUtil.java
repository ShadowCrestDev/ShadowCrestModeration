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

    /**
     * ✅ raw() liest JETZT aus der Language-Datei statt aus config.yml
     * Achtung: prefix bleibt in config.yml (so wie du es willst)
     */
    public static String raw(ShadowCrestMod plugin, String path) {
        // prefix weiter aus config
        if ("prefix".equalsIgnoreCase(path)) {
            return plugin.getConfig().getString("prefix", "");
        }

        // Alles andere aus Language
        String v = plugin.getLang().getRaw(path); // <- diese Methode muss es im LanguageManager geben
        return v == null ? "" : v;
    }

    /**
     * ✅ msg() nutzt Language + ersetzt {prefix}
     */
    public static String msg(ShadowCrestMod plugin, String path) {
        String prefix = raw(plugin, "prefix");
        String base = raw(plugin, path);

        // Wenn Key fehlt -> sichtbar machen statt "nichts"
        if (base == null || base.isEmpty()) {
            base = "{prefix}&cMissing lang key: &f" + path;
        }

        return color(base.replace("{prefix}", prefix));
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

        if (s == null || s.isEmpty()) {
            s = "{prefix}&cMissing lang key: &f" + path;
        }

        s = s.replace("{prefix}", raw(plugin, "prefix"));
        for (var e : placeholders.entrySet()) {
            s = s.replace("{" + e.getKey() + "}", e.getValue());
        }
        return color(s);
    }

    // Wie format(...) aber als Component (für kick)
    public static Component formatComponent(ShadowCrestMod plugin, String path, Map<String, String> placeholders) {
        String raw = raw(plugin, path);

        if (raw == null || raw.isEmpty()) {
            raw = "{prefix}&cMissing lang key: &f" + path;
        }

        raw = raw.replace("{prefix}", raw(plugin, "prefix"));
        for (var e : placeholders.entrySet()) {
            raw = raw.replace("{" + e.getKey() + "}", e.getValue());
        }
        raw = color(raw);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
    }

    // ✅ Multiline-sicherer Staff-Broadcast
    public static void broadcastToStaff(String permission, String message) {
        String colored = color(message);
        String[] lines = colored.split("\\r?\\n");

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(permission)) {
                for (String line : lines) {
                    if (!line.isBlank()) p.sendMessage(line);
                }
            }
        }
    }
}
