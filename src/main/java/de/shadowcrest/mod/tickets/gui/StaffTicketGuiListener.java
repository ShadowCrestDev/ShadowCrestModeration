package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.Ticket;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StaffTicketGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    // UUID -> TicketID (wenn Staff gerade Reason im Chat eingeben soll)
    public static final Map<UUID, Integer> CLOSE_REASON = new ConcurrentHashMap<>();

    public StaffTicketGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    private boolean isListGui(String title) {
        String base = MessageUtil.color(plugin.getConfig().getString("messages.staff_ticket_gui_title", "&8SCM &cTickets"));
        return title != null && title.startsWith(base);
    }

    private boolean isDetailGui(String title) {
        String base = MessageUtil.color(plugin.getConfig().getString("messages.staff_ticket_detail_title", "&8SCM &cTicket"));
        return title != null && title.startsWith(base);
    }

    private int parsePage(String title) {
        // "... Seite X"
        try {
            String clean = title.replace("§", "");
            int idx = clean.lastIndexOf("Seite ");
            if (idx == -1) return 1;
            String num = clean.substring(idx + "Seite ".length()).trim();
            return Integer.parseInt(num);
        } catch (Exception ignored) {
            return 1;
        }
    }

    private int parseTicketIdFromTitle(String title) {
        // "... #ID"
        try {
            String clean = title.replace("§", "");
            int idx = clean.lastIndexOf("#");
            if (idx == -1) return -1;
            return Integer.parseInt(clean.substring(idx + 1).trim());
        } catch (Exception ignored) {
            return -1;
        }
    }

    private int parseTicketIdFromItemName(String displayName) {
        // "Ticket #123"
        try {
            String clean = displayName.replace("§", "");
            int idx = clean.lastIndexOf("#");
            if (idx == -1) return -1;
            return Integer.parseInt(clean.substring(idx + 1).trim());
        } catch (Exception ignored) {
            return -1;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();

        // Staff GUI Permission (anpassen wie du willst)
        if (!p.hasPermission("shadowcrest.mod.ticket.staff")) return;

        if (!isListGui(title) && !isDetailGui(title)) return;

        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
        if (itemName == null) return;

        // LIST GUI
        if (isListGui(title)) {
            int page = parsePage(title);

            if (itemName.contains("Zurück")) {
                p.openInventory(StaffTicketGui.build(plugin, p, Math.max(1, page - 1)));
                return;
            }
            if (itemName.contains("Weiter")) {
                p.openInventory(StaffTicketGui.build(plugin, p, page + 1));
                return;
            }
            if (itemName.contains("Refresh")) {
                p.openInventory(StaffTicketGui.build(plugin, p, page));
                return;
            }

            int id = parseTicketIdFromItemName(itemName);
            if (id <= 0) return;

            Ticket t = plugin.getTicketManager().getTicket(id);
            if (t == null) {
                p.sendMessage(MessageUtil.color("&cTicket existiert nicht mehr."));
                p.openInventory(StaffTicketGui.build(plugin, p, page));
                return;
            }

            p.openInventory(StaffTicketDetailGui.build(plugin, t));
            return;
        }

        // DETAIL GUI
        if (isDetailGui(title)) {
            int id = parseTicketIdFromTitle(title);
            Ticket t = plugin.getTicketManager().getTicket(id);
            if (t == null) {
                p.sendMessage(MessageUtil.color("&cTicket existiert nicht mehr."));
                p.openInventory(StaffTicketGui.build(plugin, p, 1));
                return;
            }

            if (itemName.contains("Zurück")) {
                p.openInventory(StaffTicketGui.build(plugin, p, 1));
                return;
            }

            if (itemName.contains("Claim")) {
                if (!t.isClosed()) {
                    t.claim(p.getUniqueId(), p.getName());
                    plugin.getTicketManager().save();
                }
                p.openInventory(StaffTicketDetailGui.build(plugin, t));
                return;
            }

            if (itemName.contains("Unclaim")) {
                if (!t.isClosed()) {
                    t.setClaimedByUuid(null);
                    t.setClaimedByName(null);
                    t.setClaimedAt(0L);
                    // Status wieder OPEN, wenn nicht CLOSED
                    t.setStatus(de.shadowcrest.mod.tickets.TicketStatus.OPEN);
                    plugin.getTicketManager().save();
                }
                p.openInventory(StaffTicketDetailGui.build(plugin, t));
                return;
            }

            if (itemName.contains("Close")) {
                CLOSE_REASON.put(p.getUniqueId(), t.getId());
                p.closeInventory();
                p.sendMessage(MessageUtil.color(
                        plugin.getConfig().getString("prefix","") +
                                "&eBitte gib im Chat den Schließgrund ein.\n&7Tippe &fabbrechen &7zum Abbrechen."
                ));

            }
        }
    }
}
