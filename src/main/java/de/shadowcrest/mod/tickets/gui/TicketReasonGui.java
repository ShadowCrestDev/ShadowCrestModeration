package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class TicketReasonGui {

    private TicketReasonGui() {}

    public static Inventory create(ShadowCrestMod plugin) {
        String title = MessageUtil.color(plugin.getConfig().getString("messages.ticket_gui_title", "&5Ticket erstellen"));
        Inventory inv = Bukkit.createInventory(null, 27, title);

        List<String> reasons = plugin.getConfig().getStringList("tickets.reasons");
        int slot = 10;
        for (String r : reasons) {
            ItemStack it = new ItemStack(Material.PAPER);
            ItemMeta meta = it.getItemMeta();
            meta.setDisplayName(MessageUtil.color("&d" + r));
            it.setItemMeta(meta);

            inv.setItem(slot, it);
            slot++;
            if (slot == 17) slot = 19;
            if (slot >= 26) break;
        }

        return inv;
    }
}
