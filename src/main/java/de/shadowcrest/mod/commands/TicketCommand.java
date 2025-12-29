package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.gui.TicketReasonGui;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TicketCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public TicketCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfig().getBoolean("tickets.enabled", true)) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.ticket_disabled"));
            return true;
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players can create tickets.");
            return true;
        }

        p.openInventory(TicketReasonGui.create(plugin));
        return true;
    }
}
