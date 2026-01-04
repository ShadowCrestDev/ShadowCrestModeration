package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PlayerSelectGui {

    private PlayerSelectGui() {}

    // Slots im Innenbereich (ohne Rand), 4 Reihen á 7 Slots = 28 Köpfe
    private static final int[] HEAD_SLOTS = {
            10,11,12,13,14,15,16,
            19,20,21,22,23,24,25,
            28,29,30,31,32,33,34,
            37,38,39,40,41,42,43
    };

    public static Inventory build(ShadowCrestMod plugin, Player viewer) {
        String title = plugin.getLang().get("messages.gui.player_select.title");
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Rand-Deko
        ItemStack glass = item(Material.GRAY_STAINED_GLASS_PANE,
                plugin.getLang().get("messages.gui.player_select.border.name"),
                null);

        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, glass);
        }

        // Buttons
        inv.setItem(49, item(Material.BARRIER,
                plugin.getLang().get("messages.gui.player_select.button.back.name"),
                plugin.getLang().getStringList("messages.gui.player_select.button.back.lore")));

        inv.setItem(48, item(Material.BOOK,
                plugin.getLang().get("messages.gui.player_select.button.offline.name"),
                plugin.getLang().getStringList("messages.gui.player_select.button.offline.lore")));

        inv.setItem(50, item(Material.NAME_TAG,
                plugin.getLang().get("messages.gui.player_select.button.unknown.name"),
                plugin.getLang().getStringList("messages.gui.player_select.button.unknown.lore")));

        // Köpfe setzen
        int idx = 0;
        int online = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            online++;

            while (idx < HEAD_SLOTS.length) {
                int slot = HEAD_SLOTS[idx++];

                // Buttons nicht überschreiben (Sicherheit)
                if (slot == 22 || slot == 23) continue;

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta im = head.getItemMeta();

                if (im instanceof SkullMeta meta) {
                    meta.setOwningPlayer(target);

                    String headName = plugin.getLang().get(
                            "messages.gui.player_select.player_head.name",
                            Map.of("player", target.getName())
                    );

                    meta.setDisplayName(MessageUtil.color(headName));

                    List<String> lore = plugin.getLang().getStringList("messages.gui.player_select.player_head.lore");
                    meta.setLore(lore.stream().map(MessageUtil::color).toList());

                    head.setItemMeta(meta);
                }

                inv.setItem(slot, head);
                break;
            }

            if (idx >= HEAD_SLOTS.length) break;
        }

        plugin.getLogger().info("PlayerSelectGui: onlinePlayers=" + online + ", headsPlaced=" + Math.min(online, HEAD_SLOTS.length));
        return inv;
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
