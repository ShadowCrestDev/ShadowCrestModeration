package de.shadowcrest.mod.commands;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ScmCommand implements CommandExecutor {

    private final ShadowCrestMod plugin;

    public ScmCommand(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.msg(plugin, "messages.scm_usage"));
            return true;
        }

        // /scm reload
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("shadowcrest.mod.reload")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(MessageUtil.msg(plugin, "messages.reload_done"));
            return true;
        }

        // /scm info
        if (args[0].equalsIgnoreCase("info")) {
            if (!sender.hasPermission("shadowcrest.mod.info")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }

            String ver = plugin.getDescription().getVersion();
            String author = String.join(", ", plugin.getDescription().getAuthors());

            sender.sendMessage(MessageUtil.format(
                    plugin,
                    "messages.scm_info",
                    MessageUtil.ph("version", ver, "author", author)
            ));
            return true;
        }

        // /scm accept
        if (args[0].equalsIgnoreCase("accept")) {
            if (!sender.hasPermission("shadowcrest.mod.ticket.accept")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }

            var next = plugin.getTicketManager().getNextOpenTicket();
            if (next == null) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_no_tickets"));
                return true;
            }

            // safety
            if (next.getStatus() != de.shadowcrest.mod.tickets.TicketStatus.OPEN) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_no_tickets"));
                return true;
            }

            String staffName = sender.getName();
            java.util.UUID staffUuid = (sender instanceof org.bukkit.entity.Player sp) ? sp.getUniqueId() : null;

            next.claim(staffUuid, staffName);
            plugin.getTicketManager().save();

            String claimed = MessageUtil.format(
                    plugin,
                    "messages.staff_ticket_claimed",
                    MessageUtil.ph("id", next.getId(), "staff", staffName)
            );

            MessageUtil.broadcastToStaff("shadowcrest.mod.ticket.notify", claimed);

            sender.sendMessage(claimed);
            sender.sendMessage(MessageUtil.color(
                    plugin.getConfig().getString("prefix", "") +
                            "&e➡ &7Nutze: &f/scm tpticket " + next.getId()
            ));

            return true;
        }

        // /scm tpticket <id>
        if (args[0].equalsIgnoreCase("tpticket")) {
            if (!sender.hasPermission("shadowcrest.mod.ticket.tp")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }

            if (!(sender instanceof org.bukkit.entity.Player staff)) {
                sender.sendMessage("Only players can teleport.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_tp_usage"));
                return true;
            }

            int id;
            try { id = Integer.parseInt(args[1]); }
            catch (Exception ex) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_found"));
                return true;
            }

            var t = plugin.getTicketManager().getTicket(id);
            if (t == null) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_found"));
                return true;
            }

            var creator = Bukkit.getPlayer(t.getCreatorUuid());
            if (creator == null) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_creator_offline"));
                return true;
            }

            staff.teleport(creator.getLocation());
            staff.sendMessage(MessageUtil.color(plugin.getConfig().getString("prefix","") +
                    "&aTeleportiert zu &f" + creator.getName() + "&a (Ticket #" + id + ")"));
            return true;
        }

        // /scm close <id> [grund...]
        if (args[0].equalsIgnoreCase("close")) {
            if (!sender.hasPermission("shadowcrest.mod.ticket.close")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_close_usage"));
                return true;
            }

            int id;
            try { id = Integer.parseInt(args[1]); }
            catch (Exception ex) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_found"));
                return true;
            }

            var t = plugin.getTicketManager().getTicket(id);
            if (t == null) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_found"));
                return true;
            }

            if (t.isClosed()) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_already_closed"));
                return true;
            }

            String reason = "Kein Grund angegeben";
            if (args.length >= 3) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]);
                    if (i + 1 < args.length) sb.append(" ");
                }
                reason = sb.toString();
            }

            String staffName = sender.getName();
            t.close(staffName, reason);
            plugin.getTicketManager().save();

            String closedMsg = MessageUtil.format(plugin, "messages.staff_ticket_closed",
                    MessageUtil.ph("id", id, "staff", staffName, "reason", reason));

            MessageUtil.broadcastToStaff("shadowcrest.mod.ticket.notify", closedMsg);
            sender.sendMessage(closedMsg);

            // Creator informieren (wenn online)
            var creator = Bukkit.getPlayer(t.getCreatorUuid());
            if (creator != null) {
                creator.sendMessage(MessageUtil.format(plugin, "messages.ticket_closed_user",
                        MessageUtil.ph("id", id, "staff", staffName, "reason", reason)));
            }

            return true;
        }

        // /scm tickets
        if (args[0].equalsIgnoreCase("tickets")) {
            if (!sender.hasPermission("shadowcrest.mod.ticket.list")) {
                sender.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return true;
            }

            var open = plugin.getTicketManager().getOpenTickets();

            sender.sendMessage(MessageUtil.color(plugin.getConfig().getString("prefix","") +
                    "&eOffene Tickets: &f" + open.size()));

            for (var t : open) {
                String line = "&8- &e#" + t.getId()
                        + " &7von &f" + t.getCreatorName()
                        + " &7| Ziel: &f" + t.getTargetName()
                        + " &7| Grund: &f" + t.getReason()
                        + (t.isClaimed()
                        ? " &7| &aClaimed: &f" + t.getClaimedByName()
                        : " &7| &cUnclaimed");

                sender.sendMessage(MessageUtil.color(line));
            }
            return true;
        }

        sender.sendMessage(MessageUtil.msg(plugin, "messages.scm_usage"));
        return true;
    }
}
