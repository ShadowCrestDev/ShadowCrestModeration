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
        // ✅ Titel aus Language (fallback bleibt)
        String title = plugin.getLang().get("messages.ticket_gui_title");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        // ✅ Items aus Language
        inv.setItem(10, item(plugin, Material.DIAMOND_SWORD,
                "messages.gui.ticket.category.hacking.name",
                "messages.gui.ticket.category.hacking.lore"));

        inv.setItem(12, item(plugin, Material.HOPPER,
                "messages.gui.ticket.category.duping.name",
                "messages.gui.ticket.category.duping.lore"));

        inv.setItem(14, item(plugin, Material.TNT,
                "messages.gui.ticket.category.griefing.name",
                "messages.gui.ticket.category.griefing.lore"));

        inv.setItem(16, item(plugin, Material.PAPER,
                "messages.gui.ticket.category.other.name",
                "messages.gui.ticket.category.other.lore"));

        return inv;
    }

    private static ItemStack item(ShadowCrestMod plugin, Material mat, String nameKey, String loreKey) {
        ItemStack it = new ItemStack(mat);
        ItemMeta im = it.getItemMeta();
        if (im != null) {
            im.setDisplayName(MessageUtil.color(plugin.getLang().get(nameKey)));

            // ✅ Lore-Liste aus Language
            List<String> lore = plugin.getLang().getStringList(loreKey);
            im.setLore(lore.stream().map(MessageUtil::color).toList());

            it.setItemMeta(im);
        }
        return it;
    }
}
