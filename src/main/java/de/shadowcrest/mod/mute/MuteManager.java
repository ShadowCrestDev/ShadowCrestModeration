package de.shadowcrest.mod.mute;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MuteManager {

    private final ShadowCrestMod plugin;
    private final Map<UUID, MutedPlayer> mutes = new ConcurrentHashMap<>();

    private File file;
    private YamlConfiguration yaml;

    public MuteManager(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.file = new File(plugin.getDataFolder(), "mutes.yml");
        this.yaml = YamlConfiguration.loadConfiguration(file);

        mutes.clear();

        var section = yaml.getConfigurationSection("mutes");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String name = yaml.getString("mutes." + key + ".name", "Unknown");
                long until = yaml.getLong("mutes." + key + ".until", -1L);
                String reason = yaml.getString("mutes." + key + ".reason", "Muted");
                String by = yaml.getString("mutes." + key + ".by", "Console");

                MutedPlayer mp = new MutedPlayer(uuid, name, until, reason, by);

                // Auto-expire on load
                if (!mp.isPermanent() && System.currentTimeMillis() > mp.getMutedUntil()) {
                    continue;
                }

                mutes.put(uuid, mp);
            } catch (Exception ignored) {}
        }

        // cleanup file if needed
        save();
    }

    public void save() {
        if (yaml == null || file == null) return;

        yaml.set("mutes", null);

        for (MutedPlayer mp : mutes.values()) {
            String path = "mutes." + mp.getUuid();
            yaml.set(path + ".name", mp.getName());
            yaml.set(path + ".until", mp.getMutedUntil());
            yaml.set(path + ".reason", mp.getReason());
            yaml.set(path + ".by", mp.getMutedBy());
        }

        try {
            yaml.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save mutes.yml: " + e.getMessage());
        }
    }

    public boolean isMuted(UUID uuid) {
        MutedPlayer mp = mutes.get(uuid);
        if (mp == null) return false;

        if (!mp.isPermanent() && System.currentTimeMillis() > mp.getMutedUntil()) {
            // expired
            mutes.remove(uuid);
            save();
            return false;
        }
        return true;
    }

    public MutedPlayer getMute(UUID uuid) {
        if (!isMuted(uuid)) return null; // also expires if needed
        return mutes.get(uuid);
    }

    public void mute(UUID uuid, String name, long until, String reason, String by) {
        MutedPlayer mp = new MutedPlayer(uuid, name, until, reason, by);
        mutes.put(uuid, mp);
        save();
    }

    public void tickExpiredMutes() {
        long now = System.currentTimeMillis();

        Iterator<MutedPlayer> it = mutes.values().iterator();
        while (it.hasNext()) {
            MutedPlayer mp = it.next();

            if (!mp.isPermanent() && now > mp.getMutedUntil()) {
                it.remove();

                var p = plugin.getServer().getPlayer(mp.getUuid());
                if (p != null && p.isOnline()) {
                    p.sendMessage(plugin.getLang().get("messages.unmuted_you"));
                }
            }
        }

        save();
    }

    public boolean unmute(UUID uuid) {
        MutedPlayer removed = mutes.remove(uuid);
        if (removed != null) {
            save();
            return true;
        }
        return false;
    }

    public String getRemainingText(MutedPlayer mp) {
        if (mp == null) return "";
        if (mp.isPermanent()) return "Permanent";
        long remaining = mp.getMutedUntil() - System.currentTimeMillis();
        return DurationUtil.formatRemaining(Math.max(0, remaining));
    }
}
