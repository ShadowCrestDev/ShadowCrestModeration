package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.Ticket;
import de.shadowcrest.mod.tickets.TicketStatus;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.Bukkit;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaffTicketGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    // (kann bleiben, falls du Chat-Close weiterhin nutzt)
    public static final Map<UUID, Integer> CLOSE_REASON = new ConcurrentHashMap<>();

    public StaffTicketGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    private boolean isListGui(String title) {
        if (title == null) return false;

        String pattern = plugin.getLang().get("messages.gui.staff_ticket.title"); // ... {page}
        int idx = pattern.indexOf("{page}");
        String prefix = idx >= 0 ? pattern.substring(0, idx) : pattern;
        return title.startsWith(prefix);
    }

    private boolean isDetailGui(String title) {
        if (title == null) return false;

        String pattern = plugin.getLang().get("messages.gui.staff_ticket_detail.title"); // ... #{id}
        int idx = pattern.indexOf("{id}");
        String prefix = idx >= 0 ? pattern.substring(0, idx) : pattern;
        return title.startsWith(prefix);
    }

    private int parseTrailingNumber(String title) {
        if (title == null) return -1;
        String clean = title.replace("§", "");
        Matcher m = Pattern.compile("(\\d+)\\s*$").matcher(clean);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception ignored) {}
        }
        return -1;
    }

    private int parseTicketIdFromTitleHash(String title) {
        try {
            String clean = title.replace("§", "");
            int idx = clean.lastIndexOf("#");
            if (idx == -1) return -1;
            return Integer.parseInt(clean.substring(idx + 1).trim());
        } catch (Exception ignored) {
            return -1;
        }
    }

    private int parseTicketIdFromItemNameHash(String displayName) {
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

        if (!p.hasPermission("shadowcrest.mod.ticket.staff")) return;

        String title = e.getView().getTitle();
        boolean list = isListGui(title);
        boolean detail = isDetailGui(title);
        if (!list && !detail) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCurrentItem().getItemMeta() == null) return;

        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
        if (itemName == null) itemName = "";

        int rawSlot = e.getRawSlot();
        Material type = e.getCurrentItem().getType();

        // =====================
        // LIST GUI
        // =====================
        if (list) {
            int page = parseTrailingNumber(title);
            if (page <= 0) page = 1;

            // Controls: Slot 45=prev, 49=refresh, 53=next
            if (rawSlot == 45 && type == Material.ARROW) {
                p.openInventory(StaffTicketGui.build(plugin, p, Math.max(1, page - 1)));
                return;
            }
            if (rawSlot == 49 && type == Material.BOOK) {
                p.openInventory(StaffTicketGui.build(plugin, p, page));
                return;
            }
            if (rawSlot == 53 && type == Material.ARROW) {
                p.openInventory(StaffTicketGui.build(plugin, p, page + 1));
                return;
            }

            // Ticket klicken
            int id = parseTicketIdFromItemNameHash(itemName);
            if (id <= 0) return;

            Ticket t = plugin.getTicketManager().getTicket(id);
            if (t == null) {
                p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_exists"));
                p.openInventory(StaffTicketGui.build(plugin, p, page));
                return;
            }

            p.openInventory(StaffTicketDetailGui.build(plugin, t));
            return;
        }

        // =====================
        // DETAIL GUI
        // =====================
        int id = parseTicketIdFromTitleHash(title);
        Ticket t = plugin.getTicketManager().getTicket(id);

        if (t == null) {
            p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_exists"));
            p.openInventory(StaffTicketGui.build(plugin, p, 1));
            return;
        }

        // Slot 22 = Back
        if (rawSlot == 22 && type == Material.BARRIER) {
            p.openInventory(StaffTicketGui.build(plugin, p, 1));
            return;
        }

        // Slot 15 = Teleport
        if (rawSlot == 15 && type == Material.ENDER_PEARL) {
            var creator = org.bukkit.Bukkit.getPlayer(t.getCreatorUuid());
            if (creator == null) {
                p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_creator_offline"));
                return;
            }
            p.teleport(creator.getLocation());
            p.sendMessage(MessageUtil.format(
                    plugin,
                    "messages.staff_ticket_teleported",
                    MessageUtil.ph("player", creator.getName(), "id", t.getId())
            ));
            return;
        }

        // Slot 11 = Claim/Unclaim
        if (rawSlot == 11 && type == Material.LIME_WOOL) {
            if (!t.isClosed()) {
                if (t.isClaimed()) {
                    // Unclaim
                    t.setClaimedByUuid(null);
                    t.setClaimedByName(null);
                    t.setClaimedAt(0L);
                    t.setStatus(TicketStatus.OPEN);

                    // optional: toggle-mode beim staff aus
                    plugin.getTicketChatManager().disableChat(p.getUniqueId());

                } else {
                    // Claim
                    t.claim(p.getUniqueId(), p.getName());

                    // ✅ DISCORD WEBHOOK: Ticket claimed
                    if (plugin.getConfig().getBoolean("discord.enabled", false)
                            && plugin.getConfig().getBoolean("discord.events.tickets.claimed", true)) {

                        String text = plugin.getLang().get("messages.discord.ticket_claimed", java.util.Map.of(
                                "id", String.valueOf(t.getId()),
                                "staff", p.getName()
                        ));

                        plugin.getDiscord().sendPlainAsync(text);
                    }


                    String claimed = MessageUtil.format(
                            plugin,
                            "messages.staff_ticket_claimed",
                            MessageUtil.ph("id", t.getId(), "staff", p.getName())
                    );
                    MessageUtil.broadcastToStaff("shadowcrest.mod.ticket.notify", claimed);
                    p.sendMessage(claimed);

                    // ✅ NEU: Ticket-Chat-Session starten
                    plugin.getTicketChatManager().startSession(
                            t.getId(),
                            t.getCreatorUuid(),
                            p.getUniqueId()
                    );

                    // ✅ NEU: Info an Supporter + Ersteller senden
                    plugin.getTicketChatManager().sendClaimInfo(
                            t.getId(),
                            p,
                            Bukkit.getPlayer(t.getCreatorUuid())
                    );
                }

                plugin.getTicketManager().save();
            }

            // GUI neu öffnen/refresh (optional)
            p.openInventory(StaffTicketDetailGui.build(plugin, t));
            return;
        }

// Slot 14 = Actions
        if (rawSlot == 14 && type == Material.ANVIL) {
            plugin.getTicketContext().set(p.getUniqueId(), t.getId()); // ✅ Kontext setzen
            p.openInventory(StaffTicketActionsGui.build(plugin, t));
            return;
        }


        // Slot 16 = Close (öffnet Close-GUI)
        if (rawSlot == 16 && type == Material.RED_WOOL) {
            p.openInventory(StaffTicketCloseGui.build(plugin, t.getId()));
            return;
        }
    }
}

