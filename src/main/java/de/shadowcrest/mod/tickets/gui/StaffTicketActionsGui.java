package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class StaffTicketActionsGui {

    private static NamespacedKey KEY_TICKET_ID;
    private static NamespacedKey KEY_ACTION;
    private static NamespacedKey KEY_REASON;
    private static NamespacedKey KEY_DURATION;

    // ✅ GUI marker key
    private static NamespacedKey KEY_GUI_TYPE;

    public static void initKeys(ShadowCrestMod plugin) {
        KEY_TICKET_ID = new NamespacedKey(plugin, "ticketId");
        KEY_ACTION = new NamespacedKey(plugin, "action");
        KEY_REASON = new NamespacedKey(plugin, "reason");
        KEY_DURATION = new NamespacedKey(plugin, "duration");

        KEY_GUI_TYPE = new NamespacedKey(plugin, "guiType");
    }

    public static Inventory build(ShadowCrestMod plugin, Ticket t) {
        String title = plugin.getLang().get(
                "messages.gui.staff_ticket_actions.title",
                Map.of("id", String.valueOf(t.getId()))
        );

        Inventory inv = Bukkit.createInventory(null, 27, title);

        // ✅ schwarzer Rand
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta gm = glass.getItemMeta();
        if (gm != null) {
            gm.setDisplayName(" ");
            glass.setItemMeta(gm);
        }

        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, glass);
            }
        }

        // ✅ Marker in Slot 0 (liegt sowieso im Rand)
        inv.setItem(0, marker(plugin));

        // Back (Slot 18)
        inv.setItem(18, createButton(
                Material.ARROW,
                plugin.getLang().get("messages.gui.staff_ticket_actions.button.back.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_actions.button.back.lore"),
                t.getId(), "BACK", null, null
        ));

        // WARN (Slot 10)
        inv.setItem(10, createButton(
                Material.PAPER,
                plugin.getLang().get("messages.gui.staff_ticket_actions.button.warn.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_actions.button.warn.lore"),
                t.getId(), "WARN", plugin.getLang().get("messages.gui.staff_ticket_actions.default_reason"), null
        ));

        // KICK (Slot 11)
        inv.setItem(11, createButton(
                Material.IRON_BOOTS,
                plugin.getLang().get("messages.gui.staff_ticket_actions.button.kick.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_actions.button.kick.lore"),
                t.getId(), "KICK", plugin.getLang().get("messages.gui.staff_ticket_actions.default_reason"), null
        ));

        // TEMPBAN 1h (Slot 13)
        inv.setItem(13, createButton(
                Material.CLOCK,
                plugin.getLang().get("messages.gui.staff_ticket_actions.button.tempban_1h.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_actions.button.tempban_1h.lore"),
                t.getId(), "TEMPBAN", plugin.getLang().get("messages.gui.staff_ticket_actions.default_reason"), "1h"
        ));

        // TEMPBAN 1d (Slot 14)
        inv.setItem(14, createButton(
                Material.CLOCK,
                plugin.getLang().get("messages.gui.staff_ticket_actions.button.tempban_1d.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_actions.button.tempban_1d.lore"),
                t.getId(), "TEMPBAN", plugin.getLang().get("messages.gui.staff_ticket_actions.default_reason"), "1d"
        ));

        // BAN (Slot 15)
        inv.setItem(15, createButton(
                Material.BARRIER,
                plugin.getLang().get("messages.gui.staff_ticket_actions.button.ban.name"),
                plugin.getLang().getStringList("messages.gui.staff_ticket_actions.button.ban.lore"),
                t.getId(), "BAN", plugin.getLang().get("messages.gui.staff_ticket_actions.default_reason"), null
        ));

        return inv;
    }


    // ✅ Marker Item – sieht aus wie Rand
    private static ItemStack marker(ShadowCrestMod plugin) {
        ItemStack it = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = it.getItemMeta();
        if (meta == null) return it;

        meta.setDisplayName(" ");
        meta.getPersistentDataContainer().set(KEY_GUI_TYPE, PersistentDataType.STRING, "STAFF_TICKET_ACTIONS");
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack createButton(Material mat, String name, List<String> lore,
                                          int ticketId, String action, String reason, String duration) {

        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        if (meta == null) return it;

        meta.setDisplayName(name);
        meta.setLore(lore);

        var pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_TICKET_ID, PersistentDataType.INTEGER, ticketId);
        pdc.set(KEY_ACTION, PersistentDataType.STRING, action);
        if (reason != null) pdc.set(KEY_REASON, PersistentDataType.STRING, reason);
        if (duration != null) pdc.set(KEY_DURATION, PersistentDataType.STRING, duration);

        it.setItemMeta(meta);
        return it;
    }

    // ✅ GUI-Erkennung über Marker
    public static boolean isActionsGui(Inventory inv) {
        if (inv == null) return false;
        ItemStack marker = inv.getItem(0);
        if (marker == null || marker.getType() == Material.AIR) return false;

        ItemMeta meta = marker.getItemMeta();
        if (meta == null) return false;

        String type = meta.getPersistentDataContainer().get(KEY_GUI_TYPE, PersistentDataType.STRING);
        return "STAFF_TICKET_ACTIONS".equals(type);
    }

    public static Integer getTicketId(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(KEY_TICKET_ID, PersistentDataType.INTEGER);
    }

    public static String getAction(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(KEY_ACTION, PersistentDataType.STRING);
    }

    public static String getDuration(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(KEY_DURATION, PersistentDataType.STRING);
    }

    public static String getReason(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(KEY_REASON, PersistentDataType.STRING);
    }
}
