package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TicketChatToggleCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public TicketChatToggleCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        // /tc off
        if (args.length >= 1 && args[0].equalsIgnoreCase("off")) {
            plugin.getTicketChatManager().disableChat(p.getUniqueId());
            plugin.getTicketChatManager().sendModeDisabled(p);
            return true;
        }

        // /tc 12
        if (args.length >= 1) {
            int ticketId;
            try {
                ticketId = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                p.sendMessage("§cUsage: /tc [id|off]");
                return true;
            }

            if (plugin.getTicketChatManager().enableChat(p.getUniqueId(), ticketId)) {
                plugin.getTicketChatManager().sendModeEnabled(p, ticketId);
            } else {
                plugin.getTicketChatManager().sendModeEnableFailed(p);
            }
            return true;
        }

        // /tc  -> toggle anhand lastTicket
        Integer last = plugin.getTicketChatManager().getLastTicket(p.getUniqueId());
        if (last == null) {
            plugin.getTicketChatManager().sendNoLastTicket(p);
            return true;
        }

        // Wenn schon aktiv auf dieses Ticket -> aus, sonst an
        Integer active = plugin.getTicketChatManager().getActiveChat(p.getUniqueId());
        if (active != null && active == (int) last) {
            plugin.getTicketChatManager().disableChat(p.getUniqueId());
            plugin.getTicketChatManager().sendModeDisabled(p);
        } else {
            if (plugin.getTicketChatManager().enableChat(p.getUniqueId(), last)) {
                plugin.getTicketChatManager().sendModeEnabled(p, last);
            } else {
                plugin.getTicketChatManager().sendModeEnableFailed(p);
            }
        }

        return true;
    }
}
