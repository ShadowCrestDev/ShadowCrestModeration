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

public class StaffTicketCloseGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public StaffTicketCloseGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    private boolean isCloseGui(String title) {
        if (title == null) return false;

        // Titel-Pattern: messages.gui.staff_ticket_close.title = "... #{id} ..."
        String pattern = plugin.getLang().get("messages.gui.staff_ticket_close.title");
        int idx = pattern.indexOf("{id}");
        String prefix = idx >= 0 ? pattern.substring(0, idx) : pattern;

        return title.startsWith(prefix);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        if (!p.hasPermission("shadowcrest.mod.ticket.staff")) return;

        String title = e.getView().getTitle();
        if (!isCloseGui(title)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        int slot = e.getRawSlot();
        Material type = e.getCurrentItem().getType();

        // Back (Slot 10)
        if (slot == 10 && type == Material.ARROW) {
            int ticketId = StaffTicketCloseGui.getTicketId(plugin, e.getCurrentItem());
            Ticket t = plugin.getTicketManager().getTicket(ticketId);

            if (t == null) {
                p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_exists"));
                p.openInventory(StaffTicketGui.build(plugin, p, 1));
                return;
            }

            p.openInventory(StaffTicketDetailGui.build(plugin, t));
            return;
        }

        // Close reasons (Slots 12-16, RED_DYE)
        if (type != Material.RED_DYE) return;

        int ticketId = StaffTicketCloseGui.getTicketId(plugin, e.getCurrentItem());
        String reason = StaffTicketCloseGui.getReason(plugin, e.getCurrentItem());

        if (ticketId <= 0 || reason == null || reason.isBlank()) return;

        Ticket t = plugin.getTicketManager().getTicket(ticketId);
        if (t == null) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_exists"));
            p.openInventory(StaffTicketGui.build(plugin, p, 1));
            return;
        }

        // ✅ Safety: nicht doppelt schließen
        if (t.isClosed()) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_already_closed"));
            p.openInventory(StaffTicketGui.build(plugin, p, 1));
            return;
        }

        t.close(p.getName(), reason);
        plugin.getTicketManager().save();

        // ✅ DISCORD WEBHOOK: Ticket closed
        if (plugin.getConfig().getBoolean("discord.enabled", false)
                && plugin.getConfig().getBoolean("discord.events.tickets.closed", true)) {

            String text = plugin.getLang().get("messages.discord.ticket_closed", java.util.Map.of(
                    "id", String.valueOf(t.getId()),
                    "staff", p.getName(),
                    "reason", reason
            ));

            plugin.getDiscord().sendPlainAsync(text);
        }

        // ✅ Auto-Disable: Chat-Session schließen + Toggle-Modus aus
        plugin.getTicketChatManager().closeSession(t.getId());

        // ✅ Optional: Hinweis an beide, dass Chat-Modus beendet ist
        plugin.getTicketChatManager().sendSessionClosed(p, t.getId());

        var creator = Bukkit.getPlayer(t.getCreatorUuid());
        if (creator != null) {
            plugin.getTicketChatManager().sendSessionClosed(creator, t.getId());
        }

        p.sendMessage(MessageUtil.format(
                plugin,
                "messages.staff_ticket_closed_done",
                MessageUtil.ph("id", t.getId())
        ));

        p.openInventory(StaffTicketGui.build(plugin, p, 1));
    }
}
