package de.shadowcrest.mod.language;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class LanguageManager {

    private final JavaPlugin plugin;

    private String activeLangCode = "de_DE";
    private YamlConfiguration lang;          // aktive Sprache
    private YamlConfiguration fallbackLang;  // Fallback (de_DE)

    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        // Ordner anlegen
        File langFolder = new File(plugin.getDataFolder(), "Language");
        if (!langFolder.exists()) langFolder.mkdirs();

        // Sprachdateien aus JAR kopieren (falls nicht vorhanden)
        saveResourceIfNotExists("Language/de_DE.yml");
        saveResourceIfNotExists("Language/en_US.yml");

        // Config lesen
        this.activeLangCode = plugin.getConfig().getString("language", "de_DE");

        // Fallback + aktive Sprache laden
        this.fallbackLang = loadYamlFromDataFolder(langFolder, "de_DE.yml");
        this.lang = loadYamlFromDataFolder(langFolder, activeLangCode + ".yml");

        // Wenn aktive fehlt, fallbacken
        if (this.lang == null) {
            plugin.getLogger().warning("Language file not found for '" + activeLangCode + "'. Falling back to de_DE.");
            this.activeLangCode = "de_DE";
            this.lang = this.fallbackLang;
        }

        plugin.getLogger().info("Loaded language: " + this.activeLangCode);
    }

    public void reload() {
        plugin.reloadConfig();
        init();
    }

    /** ✅ Raw Zugriff (ohne Prefix, ohne Farben) */
    public String getRaw(String key) {
        String raw = null;
        if (lang != null) raw = lang.getString(key);
        if (raw == null && fallbackLang != null) raw = fallbackLang.getString(key);
        return raw; // kann null sein -> MessageUtil macht dann Missing-Key Anzeige
    }

    public String get(String key) {
        return get(key, Map.of());
    }

    public String get(String key, Map<String, String> placeholders) {
        String raw = getRaw(key);

        if (raw == null) {
            raw = "&c<missing lang key: &f" + key + "&c>";
        }

        // ✅ Prefix kommt aus config.yml (so wie du es willst)
        String prefix = plugin.getConfig().getString("prefix", "");
        raw = raw.replace("{prefix}", prefix);

        // Platzhalter ersetzen
        for (var entry : placeholders.entrySet()) {
            raw = raw.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return colorize(raw);
    }

    private String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private void saveResourceIfNotExists(String resourcePath) {
        File outFile = new File(plugin.getDataFolder(), resourcePath);
        if (outFile.exists()) return;
        plugin.saveResource(resourcePath, false);
    }

    private YamlConfiguration loadYamlFromDataFolder(File langFolder, String fileName) {
        File f = new File(langFolder, fileName);
        if (!f.exists()) return null;
        return YamlConfiguration.loadConfiguration(f);
    }

    public String getActiveLangCode() {
        return activeLangCode;
    }
    public List<String> getStringList(String key) {
        List<String> list = null;

        if (lang != null) list = lang.getStringList(key);
        if ((list == null || list.isEmpty()) && fallbackLang != null) list = fallbackLang.getStringList(key);

        if (list == null) list = List.of("&c<missing lang key: &f" + key + "&c>");

        // Prefix aus config.yml
        String prefix = plugin.getConfig().getString("prefix", "");
        List<String> out = new ArrayList<>(list.size());

        for (String s : list) {
            if (s == null) s = "";
            s = s.replace("{prefix}", prefix);
            out.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        return out;
    }

}
