package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.gui.TicketGui;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicketCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public TicketCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.only_players"));
            return true;
        }

        // Subcommands blocken (GUI-only)
        if (args.length > 0) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_gui_only"));
            return true;
        }

        // Cooldown
        long remaining = plugin.getTicketManager().getCooldownRemainingSeconds(p.getUniqueId());
        if (remaining > 0) {
            sender.sendMessage(MessageUtil.format(plugin, "messages.ticket_cooldown",
                    MessageUtil.ph("seconds", remaining)));
            return true;
        }

        // Max offene Tickets
        int max = plugin.getConfig().getInt("tickets.max_open_per_player", 3);
        int open = plugin.getTicketManager().getOpenCount(p.getUniqueId());
        if (open >= max) {
            sender.sendMessage(MessageUtil.format(plugin, "messages.ticket_max_open",
                    MessageUtil.ph("max", max)));
            return true;
        }

        // GUI öffnen (User)
        p.openInventory(TicketGui.build(plugin));
        sender.sendMessage(MessageUtil.msg(plugin, "messages.ticket_select_category"));
        return true;
    }
}
