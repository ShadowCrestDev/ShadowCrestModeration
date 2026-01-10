package de.shadowcrest.mod.vanish;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class VanishPickupListener implements Listener {

    private final ShadowCrestMod plugin;

    public VanishPickupListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    // Paper/Spigot: generic pickup event
    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!plugin.getVanishManager().isVanished(p.getUniqueId())) return;

        e.setCancelled(true);
    }

    // Paper: more specific pickup event (extra safety)
    @EventHandler
    public void onAttemptPickup(PlayerAttemptPickupItemEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getVanishManager().isVanished(p.getUniqueId())) return;

        e.setCancelled(true);
    }
}
