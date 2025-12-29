package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketStatus;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class StaffTicketGui {

    private StaffTicketGui() {}

    public static String title(ShadowCrestMod plugin, int id) {
        return MessageUtil.color("&8SCM &7Ticket &8#&f" + id);
    }

    public static void open(ShadowCrestMod plugin, Player staff, Ticket t) {
        Inventory inv = Bukkit.createInventory(null, 27, title(plugin, t.getId()));

        // Info Item (Papier)
        inv.setItem(13, infoItem(plugin, t));

        // Claim (Goldblock) / Already claimed
        inv.setItem(11, button(
                t.getStatus() == TicketStatus.OPEN ? Material.GOLD_BLOCK : Material.GRAY_DYE,
                t.getStatus() == TicketStatus.OPEN ? "&e&lTicket beanspruchen" : "&7Bereits beansprucht/geschlossen",
                List.of("&7Status: &f" + t.getStatus().name(),
                        "&7Claimed by: &f" + (t.isClaimed() ? t.getClaimedByName() : "-"))
        ));

        // Teleport (Ender Pearl)
        inv.setItem(15, button(
                Material.ENDER_PEARL,
                "&a&lZum Ersteller teleportieren",
                List.of("&7Teleportiert dich zum Ticket-Ersteller",
                        "&7Nur möglich wenn er online ist.")
        ));

        // Close (Barrier)
        inv.setItem(26, button(
                Material.BARRIER,
                "&c&lTicket schließen",
                List.of("&7Schließt das Ticket sofort.",
                        "&7Grund: &fGUI-Closed")
        ));

        staff.openInventory(inv);
    }

    private static ItemStack infoItem(ShadowCrestMod plugin, Ticket t) {
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta meta = it.getItemMeta();

        meta.setDisplayName(MessageUtil.color("&b&lTicket Infos"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtil.color("&7ID: &f#" + t.getId()));
        lore.add(MessageUtil.color("&7Status: &f" + t.getStatus().name()));
        lore.add(MessageUtil.color("&7Ersteller: &f" + t.getCreatorName()));
        lore.add(MessageUtil.color("&7Ziel: &f" + t.getTargetName()));
        lore.add(MessageUtil.color("&7Grund: &f" + t.getReason()));
        if (t.getInfo() != null && !t.getInfo().isBlank()) {
            lore.add(MessageUtil.color("&7Info: &f" + t.getInfo()));
        } else {
            lore.add(MessageUtil.color("&7Info: &8-"));
        }
        if (t.isClaimed()) lore.add(MessageUtil.color("&7Claimed: &a" + t.getClaimedByName()));

        meta.setLore(lore);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack button(Material mat, String name, List<String> lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(MessageUtil.color(name));
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(MessageUtil.color(s));
        meta.setLore(l);
        it.setItemMeta(meta);
        return it;
    }
}
