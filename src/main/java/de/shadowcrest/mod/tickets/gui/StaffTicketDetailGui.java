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
        String title = MessageUtil.color(plugin.getConfig().getString(
                "messages.staff_ticket_detail_title", "&8SCM &cTicket"
        )) + " #" + t.getId();

        Inventory inv = Bukkit.createInventory(null, 27, title);

        // dunkler Rand
        ItemStack glass = item(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, glass);
        }

        // Info-Paper in die Mitte
        List<String> lore = new ArrayList<>();
        lore.add("&7Ersteller: &f" + t.getCreatorName());
        lore.add("&7Ziel: &f" + (t.getTargetName() == null ? "Unbekannt" : t.getTargetName()));
        lore.add("&7Kategorie: &f" + t.getReason());
        lore.add("&7Info: &f" + (t.getInfo().isBlank() ? "-" : t.getInfo()));
        lore.add("&7Status: &f" + t.getStatus().name());
        if (t.isClaimed()) lore.add("&7Claimed von: &f" + t.getClaimedByName());
        lore.add("&7Erstellt: &f" + formatTime(t.getCreatedAt()));

        inv.setItem(13, item(Material.PAPER, "&fTicket #" + t.getId(), lore));

        // Claim / Unclaim
        if (t.isClaimed()) {
            inv.setItem(11, item(Material.LIME_WOOL, "&aUnclaim", List.of("&7Ticket wieder freigeben")));
        } else {
            inv.setItem(11, item(Material.LIME_WOOL, "&aClaim", List.of("&7Ticket annehmen")));
        }

        // Teleport
        inv.setItem(15, item(Material.ENDER_PEARL, "&bTeleport", List.of("&7Zum Ersteller teleportieren")));

        // Close
        inv.setItem(16, item(Material.RED_WOOL, "&cClose", List.of("&7Ticket schließen (Grund im Chat)")));

        // Zurück
        inv.setItem(22, item(Material.BARRIER, "&cZurück", List.of("&7Zurück zur Übersicht")));

        return inv;
    }

    private static String formatTime(long ms) {
        try {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(ms));
        } catch (Exception e) {
            return String.valueOf(ms);
        }
    }

    private static ItemStack item(Material mat, String name, List<String> lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta im = it.getItemMeta();
        if (im != null) {
            im.setDisplayName(MessageUtil.color(name));
            if (lore != null) {
                List<String> colored = new ArrayList<>();
                for (String l : lore) colored.add(MessageUtil.color(l));
                im.setLore(colored);
            }
            it.setItemMeta(im);
        }
        return it;
    }
}
