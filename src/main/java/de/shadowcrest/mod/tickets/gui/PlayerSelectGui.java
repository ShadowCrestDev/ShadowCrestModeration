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
        String title = MessageUtil.color("&8SCM &7Spieler auswählen");
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Rand-Deko
        ItemStack glass = item(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, glass);
        }

        // Buttons (fixe Plätze, damit die niemals überschrieben werden)
        inv.setItem(49, item(Material.BARRIER, "&cZurück", List.of("&7Zurück zur Ticket-Auswahl")));
        inv.setItem(48, item(Material.BOOK, "&eOffline Spieler anzeigen", List.of("&7Zeigt Spieler, die gerade offline sind")));
        inv.setItem(50, item(Material.NAME_TAG, "&cName nicht bekannt", List.of("&7Du weißt nicht, wer es war.", "&7Ticket wird ohne Ziel erstellt")));

        // Köpfe setzen (auf freie Slots, Buttons werden übersprungen)
        int idx = 0;
        int online = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            online++;

            // Optional: sich selbst nicht reporten
            // if (target.getUniqueId().equals(viewer.getUniqueId())) continue;

            while (idx < HEAD_SLOTS.length) {
                int slot = HEAD_SLOTS[idx++];
                // Buttons nicht überschreiben
                if (slot == 22 || slot == 23) continue;

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta im = head.getItemMeta();

                if (im instanceof SkullMeta meta) {
                    meta.setOwningPlayer(target);
                    meta.setDisplayName(MessageUtil.color("&e" + target.getName()));
                    meta.setLore(List.of(MessageUtil.color("&7Klicken um diesen Spieler zu reporten")));
                    head.setItemMeta(meta);
                }

                inv.setItem(slot, head);
                break;
            }

            if (idx >= HEAD_SLOTS.length) break;
        }

        // Debug (siehst du in der Konsole)
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
