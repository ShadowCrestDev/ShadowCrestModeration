package de.shadowcrest.mod.vanish;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VanishListener implements Listener {

    private final ShadowCrestMod plugin;

    public VanishListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getVanishManager().applyVanishForJoiner(e.getPlayer());
    }
}
