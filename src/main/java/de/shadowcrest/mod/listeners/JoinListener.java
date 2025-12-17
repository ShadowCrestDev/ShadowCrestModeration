package de.shadowcrest.mod.listeners;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.data.PlayerData;
import de.shadowcrest.mod.data.PlayerDataManager;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final ShadowCrestMod plugin;

    public JoinListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        boolean enabled = plugin.getConfig().getBoolean("messages.join_log.enabled", true);
        if (!enabled) return;

        Player p = e.getPlayer();
        PlayerData data = plugin.getDataManager().load(p.getUniqueId());

        int max = plugin.getConfig().getInt("storage.max_warn_list_on_join", 5);

        String listFormat = plugin.getConfig().getString(
                "messages.warn_list_format",
                " &8- &f{date} &7| &f{reason}"
        );
        String listEmpty = plugin.getConfig().getString(
                "messages.warn_list_empty",
                " &8- &7Keine"
        );

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

        String formatted = plugin.getConfig().getString("messages.join_log.format", "")
                .replace("{prefix}", plugin.getConfig().getString("prefix", ""))
                .replace("{player}", p.getName())
                .replace("{warns}", String.valueOf(data.getWarns()))
                .replace("{warn_list}", warnList);

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("shadowcrest.mod.notify")) {
                staff.sendMessage(MessageUtil.color(formatted));
            }
        }
    }
}
