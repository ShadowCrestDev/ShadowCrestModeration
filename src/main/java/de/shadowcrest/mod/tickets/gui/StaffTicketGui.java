package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.Ticket;
import de.shadowcrest.mod.tickets.TicketStatus;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public final class StaffTicketGui {

    private StaffTicketGui() {}

    public static Inventory build(ShadowCrestMod plugin, int page) {
        return build(plugin, null, page);
    }

    public static Inventory build(ShadowCrestMod plugin, org.bukkit.entity.Player viewer, int page) {
        // Titel aus Language: "... Page {page}"
        String title = plugin.getLang().get(
                "messages.gui.staff_ticket.title",
                Map.of("page", String.valueOf(page))
        );

        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Rahmen
        ItemStack glass = item(
                Material.GRAY_STAINED_GLASS_PANE,
                plugin.getLang().get("messages.gui.staff_ticket.border.name"),
                List.of()
        );
        for (int i = 0; i < 54; i++) inv.setItem(i, glass);

        // Slots für Tickets (28 Slots)
        int[] slots = {
                10,11,12,13,14,15,16,
                19,20,21,22,23,24,25,
                28,29,30,31,32,33,34,
                37,38,39,40,41,42,43
        };

        List<Ticket> list = plugin.getTicketManager().getOpenTickets();
        int perPage = slots.length;
        int maxPage = Math.max(1, (int) Math.ceil(list.size() / (double) perPage));
        if (page < 1) page = 1;
        if (page > maxPage) page = maxPage;

        int start = (page - 1) * perPage;
        int end = Math.min(list.size(), start + perPage);

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm"); // Format kannst du später auch lokalisieren

        int idx = 0;
        for (int i = start; i < end; i++) {
            Ticket t = list.get(i);

            Material mat = (t.getStatus() == TicketStatus.OPEN) ? Material.LIME_DYE : Material.ORANGE_DYE;

            String targetName = (t.getTargetName() == null || t.getTargetName().isBlank())
                    ? plugin.getLang().get("messages.gui.staff_ticket.unknown_target")
                    : t.getTargetName();

            List<String> lore = new ArrayList<>();
            lore.add(MessageUtil.color(plugin.getLang().get(
                    "messages.gui.staff_ticket.ticket.lore.id",
                    Map.of("id", String.valueOf(t.getId()))
            )));
            lore.add(MessageUtil.color(plugin.getLang().get(
                    "messages.gui.staff_ticket.ticket.lore.status",
                    Map.of("status", t.getStatus().name())
            )));
            lore.add(MessageUtil.color(plugin.getLang().get(
                    "messages.gui.staff_ticket.ticket.lore.category",
                    Map.of("category", String.valueOf(t.getReason()))
            )));
            lore.add(MessageUtil.color(plugin.getLang().get(
                    "messages.gui.staff_ticket.ticket.lore.from",
                    Map.of("player", String.valueOf(t.getCreatorName()))
            )));
            lore.add(MessageUtil.color(plugin.getLang().get(
                    "messages.gui.staff_ticket.ticket.lore.against",
                    Map.of("player", targetName)
            )));

            if (t.isClaimed()) {
                lore.add(MessageUtil.color(plugin.getLang().get(
                        "messages.gui.staff_ticket.ticket.lore.claimed",
                        Map.of("staff", String.valueOf(t.getClaimedByName()))
                )));
            }

            lore.add(MessageUtil.color(plugin.getLang().get(
                    "messages.gui.staff_ticket.ticket.lore.created",
                    Map.of("date", df.format(new Date(t.getCreatedAt())))
            )));
            lore.add(MessageUtil.color(plugin.getLang().get("messages.gui.staff_ticket.ticket.lore.click_hint")));

            String itemName = plugin.getLang().get(
                    "messages.gui.staff_ticket.ticket.name",
                    Map.of("id", String.valueOf(t.getId()))
            );

            ItemStack it = item(mat, itemName, lore);
            inv.setItem(slots[idx++], it);
        }

        // Controls (fixe Slots)
        inv.setItem(45, item(
                Material.ARROW,
                plugin.getLang().get("messages.gui.staff_ticket.controls.prev.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket.controls.prev.lore")
        ));
        inv.setItem(49, item(
                Material.BOOK,
                plugin.getLang().get("messages.gui.staff_ticket.controls.refresh.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket.controls.refresh.lore")
        ));
        inv.setItem(53, item(
                Material.ARROW,
                plugin.getLang().get("messages.gui.staff_ticket.controls.next.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket.controls.next.lore")
        ));

        inv.setItem(48, item(
                Material.PAPER,
                plugin.getLang().get(
                        "messages.gui.staff_ticket.controls.counter.name",
                        Map.of("count", String.valueOf(list.size()))
                ),
                List.of(MessageUtil.color(plugin.getLang().get(
                        "messages.gui.staff_ticket.controls.counter.lore_line",
                        Map.of("page", String.valueOf(page), "maxPage", String.valueOf(maxPage))
                )))
        ));

        return inv;
    }

    private static ItemStack item(Material mat, String name, List<String> lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta im = it.getItemMeta();
        if (im != null) {
            im.setDisplayName(MessageUtil.color(name));
            if (lore != null) im.setLore(lore);
            it.setItemMeta(im);
        }
        return it;
    }
}
