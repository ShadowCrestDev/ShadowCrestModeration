package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class StaffTicketCloseGui {

    private StaffTicketCloseGui() {}

    public static Inventory build(ShadowCrestMod plugin, int ticketId) {
        String title = MessageUtil.color("&8Ticket &7#" + ticketId + " &8- &cSchließen");
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack filler = item(Material.GRAY_STAINED_GLASS_PANE, " ", List.of());
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        inv.setItem(10, item(Material.ARROW, "&7« Zurück", List.of("&7Zurück zur Ticket-Ansicht")));

        // Gründe aus Config (fallback defaults)
        List<String> reasons = plugin.getConfig().getStringList("tickets.staff_close_reasons");
        if (reasons == null || reasons.isEmpty()) {
            reasons = List.of("Erledigt", "Falschmeldung", "Keine Beweise", "Regeln erklärt");
        }

        NamespacedKey ticketKey = new NamespacedKey(plugin, "ticket-id");
        NamespacedKey reasonKey = new NamespacedKey(plugin, "close-reason");

        int[] slots = {12,13,14,15,16};
        int idx = 0;

        for (String r : reasons) {
            if (idx >= slots.length) break;

            ItemStack it = new ItemStack(Material.RED_DYE);
            ItemMeta im = it.getItemMeta();
            if (im != null) {
                im.setDisplayName(MessageUtil.color("&c" + r));
                im.setLore(List.of(
                        MessageUtil.color("&7Klicken = schließen"),
                        MessageUtil.color("&8Grund: &f" + r)
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
