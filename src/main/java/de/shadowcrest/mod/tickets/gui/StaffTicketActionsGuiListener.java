package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.Ticket;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class StaffTicketActionsGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public StaffTicketActionsGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.hasPermission("shadowcrest.mod.ticket.staff")) return;

        // ✅ nur GUI (Top Inventory) behandeln
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        // ✅ unser Actions-GUI?
        if (!StaffTicketActionsGui.isActionsGui(e.getView().getTopInventory())) return;

        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Marker Slot 0 ignorieren
        if (e.getRawSlot() == 0) return;

        Integer ticketId = StaffTicketActionsGui.getTicketId(clicked);
        if (ticketId == null) return;

        Ticket t = plugin.getTicketManager().getTicket(ticketId);
        if (t == null) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_exists"));
            p.closeInventory();
            return;
        }

        String action = StaffTicketActionsGui.getAction(clicked);
        if (action == null) return;

        // Back
        if (action.equalsIgnoreCase("BACK")) {
            p.openInventory(StaffTicketDetailGui.build(plugin, t));
            return;
        }

        // Target muss gesetzt sein
        if (t.getTargetUuid() == null || t.getTargetName() == null || t.getTargetName().isBlank()) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_action_no_target"));
            return;
        }

        String target = t.getTargetName();

        String reason = StaffTicketActionsGui.getReason(clicked);
        if (reason == null || reason.isBlank()) reason = "Ticket";

        String duration = StaffTicketActionsGui.getDuration(clicked);

        boolean ok;
        switch (action.toUpperCase()) {
            case "WARN" -> ok = Bukkit.dispatchCommand(p, "warn " + target + " " + reason);
            case "KICK" -> ok = Bukkit.dispatchCommand(p, "kick " + target + " " + reason);
            case "BAN" -> ok = Bukkit.dispatchCommand(p, "ban " + target + " " + reason);
            case "TEMPBAN" -> {
                if (duration == null || duration.isBlank()) duration = "1h";
                ok = Bukkit.dispatchCommand(p, "tempban " + target + " " + duration + " " + reason);
            }
            default -> ok = false;
        }

        if (!ok) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_action_failed"));
            return;
        }
// ✅ Ticket-Linked Moderation: Aktion ans Ticket hängen (nur wenn Kontext + target passt)
        plugin.getTicketLinkService().logActionIfLinked(
                p.getUniqueId(),
                p.getName(),
                action,
                target,
                duration,
                reason
        );

        p.sendMessage(MessageUtil.format(
                plugin,
                "messages.ticket_action_done",
                MessageUtil.ph("action", action, "player", target)
        ));
    }
}
