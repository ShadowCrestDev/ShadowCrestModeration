package de.shadowcrest.mod.discord;

import de.shadowcrest.mod.ShadowCrestMod;

import java.util.Map;

public class ModerationNotifier {

    private final ShadowCrestMod plugin;

    public ModerationNotifier(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    private boolean discordOn(String path, boolean def) {
        return plugin.getConfig().getBoolean("discord.enabled", false)
                && plugin.getConfig().getBoolean(path, def);
    }

    public void warn(String staff, String player, String reason, int warns) {
        if (!discordOn("discord.events.moderation.warn", true)) return;

        String msg = plugin.getLang().get("messages.discord.mod_warn", Map.of(
                "staff", staff,
                "player", player,
                "reason", reason,
                "warns", String.valueOf(warns)
        ));
        plugin.getDiscord().sendPlainAsync(msg);
    }

    public void kick(String staff, String player, String reason) {
        if (!discordOn("discord.events.moderation.kick", true)) return;

        String msg = plugin.getLang().get("messages.discord.mod_kick", Map.of(
                "staff", staff,
                "player", player,
                "reason", reason
        ));
        plugin.getDiscord().sendPlainAsync(msg);
    }

    public void ban(String staff, String player, String reason) {
        if (!discordOn("discord.events.moderation.ban", true)) return;

        String msg = plugin.getLang().get("messages.discord.mod_ban", Map.of(
                "staff", staff,
                "player", player,
                "reason", reason
        ));
        plugin.getDiscord().sendPlainAsync(msg);
    }

    public void tempban(String staff, String player, String duration, String reason) {
        if (!discordOn("discord.events.moderation.tempban", true)) return;

        String msg = plugin.getLang().get("messages.discord.mod_tempban", Map.of(
                "staff", staff,
                "player", player,
                "duration", duration,
                "reason", reason
        ));
        plugin.getDiscord().sendPlainAsync(msg);
    }

    public void mute(String staff, String player, String duration, String reason) {
        if (!discordOn("discord.events.moderation.mute", true)) return;

        String msg = plugin.getLang().get("messages.discord.mod_mute", Map.of(
                "staff", staff,
                "player", player,
                "duration", duration,
                "reason", reason
        ));
        plugin.getDiscord().sendPlainAsync(msg);
    }

    public void unmute(String staff, String player) {
        if (!discordOn("discord.events.moderation.unmute", true)) return;

        String msg = plugin.getLang().get("messages.discord.mod_unmute", Map.of(
                "staff", staff,
                "player", player
        ));
        plugin.getDiscord().sendPlainAsync(msg);
    }
    public void unban(String staff, String player) {
        if (!discordOn("discord.events.moderation.unban", true)) return;

        String msg = plugin.getLang().get("messages.discord.mod_unban", Map.of(
                "staff", staff,
                "player", player
        ));
        plugin.getDiscord().sendPlainAsync(msg);
    }

}
