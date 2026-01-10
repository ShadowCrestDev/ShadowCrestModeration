package de.shadowcrest.mod.vanish;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VanishManager {

    private final ShadowCrestMod plugin;
    private final Set<UUID> vanished = ConcurrentHashMap.newKeySet();

    private File file;
    private YamlConfiguration yaml;

    public VanishManager(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    public void load() {
        file = new File(plugin.getDataFolder(), "vanish.yml");
        yaml = YamlConfiguration.loadConfiguration(file);

        vanished.clear();
        for (String s : yaml.getStringList("vanished")) {
            try {
                vanished.add(UUID.fromString(s));
            } catch (Exception ignored) {}
        }
    }

    public void save() {
        if (yaml == null || file == null) return;
        yaml.set("vanished", vanished.stream().map(UUID::toString).toList());
        try {
            yaml.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save vanish.yml: " + e.getMessage());
        }
    }

    public boolean isVanished(UUID uuid) {
        return vanished.contains(uuid);
    }

    public boolean toggle(Player p) {
        if (isVanished(p.getUniqueId())) {
            setVanished(p, false);
            return false;
        }
        setVanished(p, true);
        return true;
    }

    public void setVanished(Player p, boolean state) {
        if (state) vanished.add(p.getUniqueId());
        else vanished.remove(p.getUniqueId());

        // ✅ Invisibility + Glow (Glow sieht nur, wer dich auch sieht)
        if (state) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            p.setGlowing(true);
        } else {
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
            p.setGlowing(false);
        }

        applyVanishState(p, state);
        save();
    }


    /**
     * Hide/show player to everyone based on permission.
     */
    public void applyVanishState(Player vanishedPlayer, boolean state) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (viewer.equals(vanishedPlayer)) continue;

            if (state) {
                if (!viewer.hasPermission("shadowcrest.mod.vanish.see")) {
                    viewer.hidePlayer(plugin, vanishedPlayer);
                } else {
                    viewer.showPlayer(plugin, vanishedPlayer);
                }
            } else {
                viewer.showPlayer(plugin, vanishedPlayer);
            }
        }

        // also handle tab list visibility (recommended)
        if (state) {
            if (!vanishedPlayer.hasPermission("shadowcrest.mod.vanish.see")) {
                // doesn't matter
            }
            vanishedPlayer.setPlayerListName(null); // reset to default
        }
    }

    /**
     * Called when someone joins, so they don't see vanished players.
     */
    public void applyVanishForJoiner(Player joiner) {
        // joiner sees vanished players ONLY if they have permission
        boolean canSee = joiner.hasPermission("shadowcrest.mod.vanish.see");

        for (Player v : Bukkit.getOnlinePlayers()) {
            if (!isVanished(v.getUniqueId())) continue;

            if (!canSee) joiner.hidePlayer(plugin, v);
            else joiner.showPlayer(plugin, v);
        }

        // if joiner is vanished themselves, hide them from others
        if (isVanished(joiner.getUniqueId())) {
            applyVanishState(joiner, true);
        }
    }

    public ShadowCrestMod getPlugin() {
        return plugin;
    }
}
