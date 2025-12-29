package de.shadowcrest.mod.tickets.listeners;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TicketQuitListener implements Listener {

    private final ShadowCrestMod plugin;

    public TicketQuitListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getTicketManager().getSessions().remove(e.getPlayer().getUniqueId());
    }
}
