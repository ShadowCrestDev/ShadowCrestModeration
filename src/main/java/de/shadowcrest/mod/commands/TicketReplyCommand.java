package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicketReplyCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public TicketReplyCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage("§cUsage: /t <message>");
            return true;
        }

        Integer ticketId = plugin.getTicketChatManager().getLastTicket(p.getUniqueId());
        if (ticketId == null) {
            plugin.getTicketChatManager().sendNoLastTicket(p);
            return true;
        }

        String message = String.join(" ", args);
        plugin.getTicketChatManager().sendTicketMessage(ticketId, p, message);
        return true;
    }
}
