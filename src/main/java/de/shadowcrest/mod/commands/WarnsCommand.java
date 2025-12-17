package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.data.PlayerData;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WarnsCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public WarnsCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("shadowcrest.mod.warns")) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.warns_usage"));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);

        if (off == null || off.getUniqueId() == null) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.player_not_found"));
            return true;
        }

        UUID uuid = off.getUniqueId();
        PlayerData data = plugin.getDataManager().load(uuid);

        int warns = data.getWarns();
        List<PlayerData.WarnEntry> history = data.getWarnHistory();

        // Header
        sender.sendMessage(MessageUtil.format(
                plugin,
                "messages.warns_header",
                MessageUtil.ph("player", targetName, "warns", warns)
        ));

        if (history.isEmpty()) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.warns_empty"));
            return true;
        }

        int max = plugin.getConfig().getInt("storage.max_warn_list_on_join", 5);
        int shown = 0;

        for (int i = history.size() - 1; i >= 0 && shown < max; i--) {
            PlayerData.WarnEntry entry = history.get(i);

            String date = sdf.format(new Date(entry.timestamp));

            sender.sendMessage(MessageUtil.format(
                    plugin,
                    "messages.warns_entry",
                    MessageUtil.ph("date", date, "reason", entry.reason)
            ));
            shown++;
        }


        return true;
    }
}
