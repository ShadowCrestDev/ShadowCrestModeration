package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class TicketGui {

    private TicketGui() {}

    public static Inventory build(ShadowCrestMod plugin) {
        String title = MessageUtil.color(plugin.getConfig().getString("messages.ticket_gui_title", "&8SCM &7Tickets"));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        inv.setItem(10, item(Material.DIAMOND_SWORD, "&cHacking", List.of("&7Cheats / KillAura / Fly")));
        inv.setItem(12, item(Material.HOPPER, "&eDuping", List.of("&7Dupe / Exploit")));
        inv.setItem(14, item(Material.TNT, "&4Griefing", List.of("&7Grief / Diebstahl / Base")));
        inv.setItem(16, item(Material.PAPER, "&bSonstiges", List.of("&7Andere Probleme")));

        return inv;
    }

    private static ItemStack item(Material mat, String name, List<String> lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta im = it.getItemMeta();
        if (im != null) {
            im.setDisplayName(MessageUtil.color(name));
            im.setLore(lore.stream().map(MessageUtil::color).toList());
            it.setItemMeta(im);
        }
        return it;
    }
}
