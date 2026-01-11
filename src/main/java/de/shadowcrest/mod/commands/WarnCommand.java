package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.data.PlayerData;
import de.shadowcrest.mod.data.TimeUtil;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WarnCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public WarnCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("shadowcrest.mod.warn")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.reason_required"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        String reason = joinArgs(args, 1);

        PlayerData data = plugin.getDataManager().load(target.getUniqueId());
        data.addWarn(reason);
        plugin.getDataManager().save(data);

        int warns = data.getWarns();
        String staff = sender.getName();

        // Staff log
        MessageUtil.broadcastToStaff(
                "shadowcrest.mod.notify",
                MessageUtil.format(
                        plugin,
                        "messages.staff_action.warn",
                        MessageUtil.ph("staff", staff, "player", target.getName(), "reason", reason, "warns", warns)
                )
        );

        // ✅ Discord webhook (Warn)
        if (plugin.getModNotifier() != null) {
            plugin.getModNotifier().warn(staff, target.getName(), reason, warns);
        }

        // Sender confirmation
        sender.sendMessage(
                MessageUtil.format(
                        plugin,
                        "messages.warn_given",
                        MessageUtil.ph("player", target.getName(), "reason", reason, "warns", warns)
                )
        );

        // Player message
        String warned = MessageUtil.format(
                plugin,
                "messages.warned_screen",
                MessageUtil.ph("reason", reason, "warns", warns)
        );
        target.sendMessage(warned);

        // Auto punishment if configured
        applyAutoPunishment(target, warns);

        return true;
    }

    private void applyAutoPunishment(Player target, int warns) {
        List<?> actions = plugin.getConfig().getList("warn_settings.actions");
        if (actions == null) return;

        for (Object o : actions) {
            if (!(o instanceof Map<?, ?> map)) continue;

            int trigger = 0;
            Object wObj = map.get("warns");
            if (wObj instanceof Number n) trigger = n.intValue();

            if (trigger != warns) continue;

            Object actionObj = map.get("action");
            String action = (actionObj == null) ? "NONE" : String.valueOf(actionObj).toUpperCase();

            Object reasonObj = map.get("reason");
            String punishReason = (reasonObj == null) ? "Zu viele Verwarnungen" : String.valueOf(reasonObj);

            if (action.equals("BAN")) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), punishReason, null, "ShadowCrest");
                String screen = MessageUtil.format(
                        plugin,
                        "messages.ban_screen",
                        MessageUtil.ph("reason", punishReason)
                );
                target.kick(MessageUtil.component(screen));
                return;
            }

            if (action.equals("TEMPBAN")) {
                Object durObj = map.get("duration");
                String durationStr = (durObj == null) ? "24h" : String.valueOf(durObj);

                long ms = TimeUtil.parseDurationToMillis(durationStr);
                if (ms <= 0) return;

                Date until = new Date(System.currentTimeMillis() + ms);
                Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), punishReason, until, "ShadowCrest");

                String screen = MessageUtil.format(
                        plugin,
                        "messages.tempban_screen",
                        MessageUtil.ph("duration", durationStr, "reason", punishReason)
                );
                target.kick(MessageUtil.component(screen));
                return;
            }

            return;
        }
    }

    private String joinArgs(String[] args, int from) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < args.length; i++) {
            sb.append(args[i]);
            if (i + 1 < args.length) sb.append(" ");
        }
        return sb.toString();
    }
}
