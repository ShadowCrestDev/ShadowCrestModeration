package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.tickets.TicketStatus;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class TicketGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public TicketGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!(e.getInventory().getHolder() instanceof TicketStaffGuiHolder)) return;

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getItemMeta() == null) return;

        var pdc = item.getItemMeta().getPersistentDataContainer();
        String action = pdc.get(new NamespacedKey(plugin, "scm_action"), PersistentDataType.STRING);
        Integer ticketId = pdc.get(new NamespacedKey(plugin, "scm_ticket"), PersistentDataType.INTEGER);
        if (action == null || ticketId == null) return;

        Ticket t = plugin.getTicketManager().getTicket(ticketId);
        if (t == null) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_found"));
            p.closeInventory();
            return;
        }

        // CLAIM
        if (action.equals(StaffTicketGui.ACTION_CLAIM)) {
            if (!p.hasPermission("shadowcrest.mod.ticket.accept")) {
                p.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return;
            }
            if (t.getStatus() != TicketStatus.OPEN) {
                p.sendMessage(MessageUtil.color(plugin.getConfig().getString("prefix","") + "&cTicket ist nicht mehr OPEN."));
                return;
            }

            t.claim(p.getUniqueId(), p.getName(), System.currentTimeMillis());
            plugin.getTicketManager().save();

            String msg = MessageUtil.format(plugin, "messages.staff_ticket_claimed",
                    MessageUtil.ph("id", t.getId(), "staff", p.getName()));
            MessageUtil.broadcastToStaff("shadowcrest.mod.ticket.notify", msg);
            p.sendMessage(msg);
            return;
        }

        // TP
        if (action.equals(StaffTicketGui.ACTION_TP)) {
            if (!p.hasPermission("shadowcrest.mod.ticket.tp")) {
                p.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return;
            }

            var creator = Bukkit.getPlayer(t.getCreatorUuid());
            if (creator == null) {
                p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_creator_offline"));
                return;
            }

            p.teleport(creator.getLocation());
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("prefix","") +
                    "&aTeleportiert zu &f" + creator.getName() + "&a (Ticket #" + t.getId() + ")"));
            return;
        }

        // CLOSE -> Chat Prompt
        if (action.equals(StaffTicketGui.ACTION_CLOSE)) {
            if (!p.hasPermission("shadowcrest.mod.ticket.close")) {
                p.sendMessage(MessageUtil.msg(plugin, "messages.no_permission"));
                return;
            }

            // Session setzen: Staff soll Grund schreiben
            TicketSession s = new TicketSession(p.getUniqueId());
            s.setStep(TicketSession.Step.STAFF_CLOSE_REASON);
            s.setStaffCloseTicketId(t.getId());
            plugin.getTicketManager().getSessions().put(p.getUniqueId(), s);

            p.closeInventory();
            p.sendMessage(MessageUtil.color(plugin.getConfig().getString("prefix","") +
                    "&eBitte schreibe den Schließ-Grund in den Chat (&f-&e = kein Grund, &cAbbrechen&e = cancel)"));
        }
    }
}
