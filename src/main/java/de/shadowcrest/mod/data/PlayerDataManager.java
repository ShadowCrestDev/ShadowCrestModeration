package de.shadowcrest.mod.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerDataManager {

    private final JavaPlugin plugin;
    private final File playersDir;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playersDir = new File(plugin.getDataFolder(), "players");
        if (!playersDir.exists()) playersDir.mkdirs();
    }

    public PlayerData load(UUID uuid) {
        File f = new File(playersDir, uuid.toString() + ".yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);

        PlayerData data = new PlayerData(uuid.toString());

        // warns aus Datei laden
        int warns = yml.getInt("warns", 0);
        data.setWarns(warns);

        // warn_history "capture-safe" laden
        var list = yml.getList("warn_history", java.util.List.of());
        for (Object o : list) {
            if (!(o instanceof Map<?, ?> map)) continue;

            long ts = 0L;
            Object timeObj = map.get("time");
            if (timeObj instanceof Number n) ts = n.longValue();

            Object reasonObj = map.get("reason");
            String reason = (reasonObj == null) ? "Unbekannt" : String.valueOf(reasonObj);

            data.getWarnHistory().add(new PlayerData.WarnEntry(ts, reason));
        }

        // Sicherheit: warns mindestens so groß wie History
        int finalWarns = Math.max(data.getWarns(), data.getWarnHistory().size());
        data.setWarns(finalWarns);

        return data;
    }

    public void save(PlayerData data) {
        File f = new File(playersDir, data.getUuid() + ".yml");
        YamlConfiguration yml = new YamlConfiguration();

        yml.set("warns", data.getWarns());

        List<Map<String, Object>> list = new ArrayList<>();
        for (PlayerData.WarnEntry e : data.getWarnHistory()) {
            Map<String, Object> m = new HashMap<>();
            m.put("time", e.timestamp);
            m.put("reason", e.reason);
            list.add(m);
        }
        yml.set("warn_history", list);

        try {
            yml.save(f);
        } catch (Exception ex) {
            plugin.getLogger().severe("Konnte PlayerData nicht speichern: " + ex.getMessage());
        }
    }

    public static String formatDate(long millis) {
        if (millis <= 0) return "Unbekannt";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(millis));
    }
}
