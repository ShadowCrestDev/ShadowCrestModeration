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
        String base = MessageUtil.color(plugin.getConfig().getString("messages.staff_ticket_gui_title", "&8SCM &cTickets"));
        String title = base + MessageUtil.color(" &7Seite " + page);

        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Rahmen
        ItemStack glass = item(Material.GRAY_STAINED_GLASS_PANE, " ", List.of());
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

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        int idx = 0;
        for (int i = start; i < end; i++) {
            Ticket t = list.get(i);

            Material mat = (t.getStatus() == TicketStatus.OPEN) ? Material.LIME_DYE : Material.ORANGE_DYE;

            List<String> lore = new ArrayList<>();
            lore.add(MessageUtil.color("&7ID: &f#" + t.getId()));
            lore.add(MessageUtil.color("&7Status: &f" + t.getStatus().name()));
            lore.add(MessageUtil.color("&7Kategorie: &f" + t.getReason()));
            lore.add(MessageUtil.color("&7Von: &f" + t.getCreatorName()));
            lore.add(MessageUtil.color("&7Gegen: &f" + (t.getTargetName() == null ? "Unbekannt" : t.getTargetName())));
            if (t.isClaimed()) lore.add(MessageUtil.color("&7Claimed: &f" + t.getClaimedByName()));
            lore.add(MessageUtil.color("&7Erstellt: &f" + df.format(new Date(t.getCreatedAt()))));
            lore.add(MessageUtil.color("&8Klick: Details öffnen"));

            ItemStack it = item(mat, "&cTicket &7#" + t.getId(), lore);
            inv.setItem(slots[idx++], it);
        }

        // Controls
        inv.setItem(45, item(Material.ARROW, "&eZurück", List.of(MessageUtil.color("&7Vorherige Seite"))));
        inv.setItem(49, item(Material.BOOK, "&bRefresh", List.of(MessageUtil.color("&7Neu laden"))));
        inv.setItem(53, item(Material.ARROW, "&eWeiter", List.of(MessageUtil.color("&7Nächste Seite"))));

        inv.setItem(48, item(Material.PAPER, "&fTickets: &b" + list.size(), List.of(
                MessageUtil.color("&7Seite &f" + page + "&7/&f" + maxPage)
        )));

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
