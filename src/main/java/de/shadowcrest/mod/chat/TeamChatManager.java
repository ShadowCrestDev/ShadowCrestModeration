package de.shadowcrest.mod.chat;

import de.shadowcrest.mod.ShadowCrestMod;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamChatManager {

    private final ShadowCrestMod plugin;
    private final Set<UUID> toggled = ConcurrentHashMap.newKeySet();

    public TeamChatManager(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    public boolean isEnabled(UUID uuid) {
        return toggled.contains(uuid);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        if (enabled) toggled.add(uuid);
        else toggled.remove(uuid);
    }

    public boolean toggle(UUID uuid) {
        if (isEnabled(uuid)) {
            setEnabled(uuid, false);
            return false;
        }
        setEnabled(uuid, true);
        return true;
    }

    public void clear(UUID uuid) {
        toggled.remove(uuid);
    }

    public ShadowCrestMod getPlugin() {
        return plugin;
    }
}
