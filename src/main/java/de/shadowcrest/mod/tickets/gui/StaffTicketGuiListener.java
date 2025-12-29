package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketStatus;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StaffTicketGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public StaffTicketGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player staff)) return;

        String title = e.getView().getTitle();
        if (title == null) return;

        // Titel: "§8SCM §7Ticket §8#§f<ID>"
        String plain = title.replace("§", "");
        if (!plain.startsWith("8SCM 7Ticket 8#f")) return;

        e.setCancelled(true);

        // ID rausparsen
        int id;
        try {
            String idStr = plain.substring(plain.indexOf("#f") + 2).trim();
            id = Integer.parseInt(idStr);
        } catch (Exception ex) {
            return;
        }

        Ticket t = plugin.getTicketManager().getTicket(id);
        if (t == null) {
            staff.closeInventory();
            staff.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_found"));
            return;
        }

        int slot = e.getRawSlot();

        // Claim Button
        if (slot == 11) {
            if (!staff.hasPermission("shadowcrest.mod.ticket.accept")) {
                staff.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return;
            }
            if (t.getStatus() != TicketStatus.OPEN) {
                staff.sendMessage(MessageUtil.color(plugin.getConfig().getString("prefix","") + "&cTicket ist nicht mehr OPEN."));
                return;
            }

            t.claim(staff.getUniqueId(), staff.getName());
            plugin.getTicketManager().save();

            String claimed = MessageUtil.format(plugin, "messages.staff_ticket_claimed",
                    MessageUtil.ph("id", t.getId(), "staff", staff.getName()));
            MessageUtil.broadcastToStaff("shadowcrest.mod.ticket.notify", claimed);

            StaffTicketGui.open(plugin, staff, t);
            return;
        }

        // Teleport Button
        if (slot == 15) {
            if (!staff.hasPermission("shadowcrest.mod.ticket.tp")) {
                staff.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return;
            }

            var creator = Bukkit.getPlayer(t.getCreatorUuid());
            if (creator == null) {
                staff.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_creator_offline"));
                return;
            }

            staff.teleport(creator.getLocation());
            staff.sendMessage(MessageUtil.color(plugin.getConfig().getString("prefix","") +
                    "&aTeleportiert zu &f" + creator.getName() + "&a (Ticket #" + id + ")"));
            return;
        }

        // Close Button
        if (slot == 26) {
            if (!staff.hasPermission("shadowcrest.mod.ticket.close")) {
                staff.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return;
            }
            if (t.isClosed()) {
                staff.sendMessage(MessageUtil.color(plugin.getConfig().getString("prefix","") + "&cTicket ist bereits geschlossen."));
                return;
            }

            t.close(staff.getName(), "GUI-Closed");
            plugin.getTicketManager().save();

            String msg = MessageUtil.format(plugin, "messages.staff_ticket_closed",
                    MessageUtil.ph("id", id, "staff", staff.getName(), "reason", "GUI-Closed"));
            MessageUtil.broadcastToStaff("shadowcrest.mod.ticket.notify", msg);

            staff.closeInventory();
            staff.sendMessage(msg);

            var creator = Bukkit.getPlayer(t.getCreatorUuid());
            if (creator != null) {
                creator.sendMessage(MessageUtil.format(plugin, "messages.ticket_closed_user",
                        MessageUtil.ph("id", id, "staff", staff.getName(), "reason", "GUI-Closed")));
            }
        }
    }
}
