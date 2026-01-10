package de.shadowcrest.mod.mute;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MuteCommandBlockListener implements Listener {

    private final ShadowCrestMod plugin;
    private final Set<String> blockedLabels = new HashSet<>();

    public MuteCommandBlockListener(ShadowCrestMod plugin) {
        this.plugin = plugin;

        List<String> list = plugin.getConfig().getStringList("mute.blocked_commands");
        for (String s : list) {
            if (s == null) continue;
            s = s.trim().toLowerCase();
            if (s.isEmpty()) continue;

            // allow "msg" or "/msg"
            if (s.startsWith("/")) s = s.substring(1);

            blockedLabels.add(s);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        var p = e.getPlayer();

        if (p.hasPermission("shadowcrest.mod.mute.bypass")) return;

        var mm = plugin.getMuteManager();
        if (mm == null) return;

        if (!mm.isMuted(p.getUniqueId())) return;

        String full = e.getMessage().trim();
        if (!full.startsWith("/")) return;

        // "/msg Steve hi" -> "msg"
        String label = full.substring(1).split("\\s+")[0].toLowerCase();

        if (!blockedLabels.contains(label)) return;

        e.setCancelled(true);

        var mp = mm.getMute(p.getUniqueId());
        if (mp == null) return;

        String time = mp.isPermanent()
                ? plugin.getLang().get("messages.mute_time_permanent")
                : mm.getRemainingText(mp);

        p.sendMessage(plugin.getLang().get("messages.muted", Map.of(
                "reason", mp.getReason() == null ? "-" : mp.getReason(),
                "time", time
        )));
    }
}
