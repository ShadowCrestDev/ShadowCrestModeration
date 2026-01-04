package de.shadowcrest.mod.listeners;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.data.PlaytimeUtil;
import de.shadowcrest.mod.data.PlayerData;
import de.shadowcrest.mod.data.PlayerDataManager;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class JoinListener implements Listener {

    private final ShadowCrestMod plugin;

    public JoinListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        try {
            Player p = e.getPlayer();

            // ✅ Wenn du später togglen willst, mach dafür einen config bool:
            // join_log.enabled: true
            boolean enabled = plugin.getConfig().getBoolean("join_log.enabled", true);
            if (!enabled) return;

            PlayerData data = plugin.getDataManager().load(p.getUniqueId());

            // ✅ richtiger Pfad aus deiner config.yml
            int max = plugin.getConfig().getInt("warn_settings.storage.max_warn_list_on_join", 5);

            // ✅ aus Language (nicht config)
            String listFormat = plugin.getLang().get("warn_list_format");
            String listEmpty = plugin.getLang().get("warn_list_empty");

            String warnList;
            if (data.getWarnHistory().isEmpty()) {
                warnList = MessageUtil.color(listEmpty);
            } else {
                StringBuilder sb = new StringBuilder();
                int start = Math.max(0, data.getWarnHistory().size() - max);
                for (int i = start; i < data.getWarnHistory().size(); i++) {
                    PlayerData.WarnEntry w = data.getWarnHistory().get(i);

                    String line = listFormat
                            .replace("{date}", PlayerDataManager.formatDate(w.timestamp))
                            .replace("{reason}", w.reason);

                    sb.append(MessageUtil.color(line)).append("\n");
                }
                warnList = sb.toString().trim();
            }

            // ✅ Playtime Pfade aus deiner config.yml
            boolean showPlaytime = plugin.getConfig().getBoolean("warn_settings.playtime.show_in_join_log", true);
            String playtime = "";
            if (showPlaytime) {
                long ticks = PlaytimeUtil.getPlayTicks(p);
                String fmt = plugin.getConfig().getString("warn_settings.playtime.format", "{days}d {hours}h {minutes}m");
                playtime = PlaytimeUtil.formatPlaytime(ticks, fmt);
            }

            // ✅ JoinLog Format aus Language
            String formatted = plugin.getLang().get(
                    "join_log.format",
                    Map.of(
                            "player", p.getName(),
                            "warns", String.valueOf(data.getWarns()),
                            "playtime", playtime,
                            "warn_list", warnList
                    )
            );

            String[] lines = formatted.split("\\r?\\n");

            int sent = 0;
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("shadowcrest.mod.notify")) {
                    for (String line : lines) {
                        if (!line.isBlank()) staff.sendMessage(line);
                    }
                    sent++;
                }
            }

            plugin.getLogger().info("JoinLog sent to " + sent + " staff member(s).");

        } catch (Exception ex) {
            plugin.getLogger().severe("JoinListener ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
