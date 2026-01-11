package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.mute.DurationUtil;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;

public class MuteCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public MuteCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("shadowcrest.mod.mute")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.mute_usage"));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);

        boolean unknown = (off == null || (!off.hasPlayedBefore() && off.getName() == null));
        if (unknown || off.getUniqueId() == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        // duration detection
        Long dur = null;
        String reason;

        if (args.length >= 2) {
            dur = DurationUtil.parseToMillis(args[1]);
        }

        if (dur != null) {
            // /mute <player> <duration> [reason...]
            if (args.length >= 3) {
                reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length)).trim();
            } else {
                reason = plugin.getLang().get("messages.mute_default_reason");
            }
        } else {
            // /mute <player> [reason...]
            if (args.length >= 2) {
                reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();
            } else {
                reason = plugin.getLang().get("messages.mute_default_reason");
            }
        }

        if (reason.isBlank()) reason = plugin.getLang().get("messages.mute_default_reason");

        long until;
        String durationText;

        if (dur == null) {
            until = -1L; // permanent
            durationText = plugin.getLang().get("messages.mute_time_permanent");
        } else {
            until = System.currentTimeMillis() + dur;
            durationText = args[1];
        }

        String finalName = (off.getName() == null ? targetName : off.getName());
        String staff = sender.getName();

        plugin.getMuteManager().mute(off.getUniqueId(), finalName, until, reason, staff);

        // ✅ Discord webhook
        if (plugin.getModNotifier() != null) {
            plugin.getModNotifier().mute(staff, finalName, durationText, reason);
        }

        // notify sender
        sender.sendMessage(plugin.getLang().get("messages.mute_done",
                Map.of(
                        "player", finalName,
                        "duration", durationText,
                        "reason", reason
                )
        ));

        // notify target if online
        if (off.isOnline() && off.getPlayer() != null) {
            Player tp = off.getPlayer();

            String remaining = (until < 0)
                    ? plugin.getLang().get("messages.mute_time_permanent")
                    : DurationUtil.formatRemaining(until - System.currentTimeMillis());

            tp.sendMessage(plugin.getLang().get("messages.muted",
                    Map.of("reason", reason, "time", remaining)));
        }

        return true;
    }
}
