package de.shadowcrest.mod.mute;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

public class MuteChatListener implements Listener {

    private final ShadowCrestMod plugin;

    public MuteChatListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onChat(AsyncPlayerChatEvent e) {
        var p = e.getPlayer();

        if (p.hasPermission("shadowcrest.mod.mute.bypass")) return;

        var muteManager = plugin.getMuteManager();
        if (muteManager == null) return;

        if (!muteManager.isMuted(p.getUniqueId())) return;

        var mp = muteManager.getMute(p.getUniqueId());
        if (mp == null) return;

        // 🔒 HARTE BLOCKADE
        e.setCancelled(true);
        e.getRecipients().clear();

        String remaining = mp.isPermanent()
                ? plugin.getLang().get("messages.mute_time_permanent")
                : muteManager.getRemainingText(mp);

        p.sendMessage(plugin.getLang().get(
                "messages.muted",
                java.util.Map.of(
                        "reason", mp.getReason() == null ? "-" : mp.getReason(),
                        "time", remaining
                )
        ));
    }

}
