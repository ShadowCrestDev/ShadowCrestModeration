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
import java.util.*;

public final class StaffTicketDetailGui {

    private StaffTicketDetailGui() {}

    public static Inventory build(ShadowCrestMod plugin, Ticket t) {
        String title = plugin.getLang().get(
                "messages.gui.staff_ticket_detail.title",
                Map.of("id", String.valueOf(t.getId()))
        );

        Inventory inv = Bukkit.createInventory(null, 27, title);

        // dunkler Rand
        ItemStack glass = item(
                Material.BLACK_STAINED_GLASS_PANE,
                plugin.getLang().get("messages.gui.staff_ticket_detail.border.name"),
                null
        );

        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, glass);
        }

        // Info-Paper in die Mitte
        String target = (t.getTargetName() == null || t.getTargetName().isBlank())
                ? plugin.getLang().get("messages.gui.staff_ticket_detail.unknown_target")
                : t.getTargetName();

        String info = (t.getInfo() == null || t.getInfo().isBlank()) ? "-" : t.getInfo();

        List<String> lore = new ArrayList<>();
        lore.add(MessageUtil.color(plugin.getLang().get(
                "messages.gui.staff_ticket_detail.info.creator",
                Map.of("player", String.valueOf(t.getCreatorName()))
        )));
        lore.add(MessageUtil.color(plugin.getLang().get(
                "messages.gui.staff_ticket_detail.info.target",
                Map.of("player", target)
        )));
        lore.add(MessageUtil.color(plugin.getLang().get(
                "messages.gui.staff_ticket_detail.info.category",
                Map.of("category", String.valueOf(t.getReason()))
        )));
        lore.add(MessageUtil.color(plugin.getLang().get(
                "messages.gui.staff_ticket_detail.info.extra",
                Map.of("info", info)
        )));
        lore.add(MessageUtil.color(plugin.getLang().get(
                "messages.gui.staff_ticket_detail.info.status",
                Map.of("status", t.getStatus().name())
        )));

        if (t.isClaimed()) {
            lore.add(MessageUtil.color(plugin.getLang().get(
                    "messages.gui.staff_ticket_detail.info.claimed_by",
                    Map.of("staff", String.valueOf(t.getClaimedByName()))
            )));
        }

        lore.add(MessageUtil.color(plugin.getLang().get(
                "messages.gui.staff_ticket_detail.info.created",
                Map.of("date", formatTime(t.getCreatedAt()))
        )));

        String paperName = plugin.getLang().get(
                "messages.gui.staff_ticket_detail.ticket_item.name",
                Map.of("id", String.valueOf(t.getId()))
        );

        inv.setItem(13, item(Material.PAPER, paperName, lore));

        // Claim / Unclaim (Slot 11)
        if (t.isClaimed()) {
            inv.setItem(11, item(
                    Material.LIME_WOOL,
                    plugin.getLang().get("messages.gui.staff_ticket_detail.button.unclaim.name"),
                    plugin.getLang().getStringList("messages.gui.staff_ticket_detail.button.unclaim.lore")
            ));
        } else {
            inv.setItem(11, item(
                    Material.LIME_WOOL,
                    plugin.getLang().get("messages.gui.staff_ticket_detail.button.claim.name"),
                    plugin.getLang().getStringList("messages.gui.staff_ticket_detail.button.claim.lore")
            ));
        }

        // Teleport (Slot 15)
        inv.setItem(15, item(
                Material.ENDER_PEARL,
                plugin.getLang().get("messages.gui.staff_ticket_detail.button.teleport.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_detail.button.teleport.lore")
        ));

        // Close (Slot 16)
        inv.setItem(16, item(
                Material.RED_WOOL,
                plugin.getLang().get("messages.gui.staff_ticket_detail.button.close.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_detail.button.close.lore")
        ));

        // Zurück (Slot 22)
        inv.setItem(22, item(
                Material.BARRIER,
                plugin.getLang().get("messages.gui.staff_ticket_detail.button.back.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_detail.button.back.lore")
        ));

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
