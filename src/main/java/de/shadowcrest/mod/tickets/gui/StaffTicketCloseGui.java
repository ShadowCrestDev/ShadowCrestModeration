package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public final class StaffTicketCloseGui {

    private StaffTicketCloseGui() {}

    public static Inventory build(ShadowCrestMod plugin, int ticketId) {
        String title = plugin.getLang().get(
                "messages.gui.staff_ticket_close.title",
                Map.of("id", String.valueOf(ticketId))
        );
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack filler = item(
                Material.GRAY_STAINED_GLASS_PANE,
                plugin.getLang().get("messages.gui.staff_ticket_close.border.name"),
                List.of()
        );
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // Back
        inv.setItem(10, item(
                Material.ARROW,
                plugin.getLang().get("messages.gui.staff_ticket_close.button.back.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_close.button.back.lore")
        ));

        // Gründe aus Language
        List<String> reasons = plugin.getLang().getStringList("messages.gui.staff_ticket_close.reasons");
        if (reasons == null || reasons.isEmpty()) {
            // Fallback (sollte nie passieren, aber sicher ist sicher)
            reasons = List.of("Done", "False report", "No evidence", "Rules explained");
        }

        NamespacedKey ticketKey = new NamespacedKey(plugin, "ticket-id");
        NamespacedKey reasonKey = new NamespacedKey(plugin, "close-reason");

        int[] slots = {12, 13, 14, 15, 16};
        int idx = 0;

        for (String r : reasons) {
            if (idx >= slots.length) break;

            ItemStack it = new ItemStack(Material.RED_DYE);
            ItemMeta im = it.getItemMeta();
            if (im != null) {
                im.setDisplayName(MessageUtil.color(
                        plugin.getLang().get("messages.gui.staff_ticket_close.reason_item.name", Map.of("reason", r))
                ));

                im.setLore(List.of(
                        MessageUtil.color(plugin.getLang().get("messages.gui.staff_ticket_close.reason_item.lore_click")),
                        MessageUtil.color(plugin.getLang().get("messages.gui.staff_ticket_close.reason_item.lore_reason", Map.of("reason", r)))
                ));

                im.getPersistentDataContainer().set(ticketKey, PersistentDataType.INTEGER, ticketId);
                im.getPersistentDataContainer().set(reasonKey, PersistentDataType.STRING, r);
                it.setItemMeta(im);
            }

            inv.setItem(slots[idx++], it);
        }

        return inv;
    }

    public static int getTicketId(ShadowCrestMod plugin, ItemStack it) {
        if (it == null || it.getItemMeta() == null) return -1;
        NamespacedKey key = new NamespacedKey(plugin, "ticket-id");
        Integer id = it.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        return id == null ? -1 : id;
    }

    public static String getReason(ShadowCrestMod plugin, ItemStack it) {
        if (it == null || it.getItemMeta() == null) return null;
        NamespacedKey key = new NamespacedKey(plugin, "close-reason");
        return it.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
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
