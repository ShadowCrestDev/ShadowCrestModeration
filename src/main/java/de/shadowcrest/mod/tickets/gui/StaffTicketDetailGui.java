package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.Ticket;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class StaffTicketDetailGui {

    private StaffTicketDetailGui() {}

    public static Inventory build(ShadowCrestMod plugin, Ticket t) {
        String base = MessageUtil.color(plugin.getConfig().getString("messages.staff_ticket_detail_title", "&8SCM &cTicket"));
        String title = base + MessageUtil.color(" &7#" + t.getId());

        Inventory inv = Bukkit.createInventory(null, 27, title);

        // Info Item
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtil.color("&7Status: &f" + t.getStatus().name()));
        lore.add(MessageUtil.color("&7Kategorie: &f" + t.getReason()));
        lore.add(MessageUtil.color("&7Von: &f" + t.getCreatorName()));
        lore.add(MessageUtil.color("&7Gegen: &f" + (t.getTargetName() == null ? "Unbekannt" : t.getTargetName())));
        lore.add(MessageUtil.color("&7Info: &f" + (t.getInfo().isBlank() ? "-" : t.getInfo())));
        lore.add(MessageUtil.color("&7Erstellt: &f" + df.format(new Date(t.getCreatedAt()))));
        if (t.isClaimed()) lore.add(MessageUtil.color("&7Claimed: &f" + t.getClaimedByName()));

        inv.setItem(13, item(Material.PAPER, "&cTicket &7#" + t.getId(), lore));

        inv.setItem(10, item(Material.LIME_CONCRETE, "&aClaim", List.of(MessageUtil.color("&7Ticket übernehmen"))));
        inv.setItem(11, item(Material.YELLOW_CONCRETE, "&eUnclaim", List.of(MessageUtil.color("&7Claim entfernen"))));
        inv.setItem(16, item(Material.RED_CONCRETE, "&cClose", List.of(MessageUtil.color("&7Ticket schließen (Reason im Chat)"))));

        inv.setItem(22, item(Material.ARROW, "&7Zurück", List.of(MessageUtil.color("&7Zur Ticketliste"))));

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
