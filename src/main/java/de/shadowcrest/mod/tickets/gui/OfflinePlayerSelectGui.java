package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class OfflinePlayerSelectGui {

    private OfflinePlayerSelectGui() {}

    // wie viele Köpfe pro Seite (Innenbereich: Slots 10-16, 19-25, 28-34, 37-43) = 28
    private static final int PAGE_SIZE = 28;

    public static Inventory build(ShadowCrestMod plugin, Player viewer, int page) {
        if (page < 1) page = 1;

        String title = MessageUtil.color("&8SCM &7Offline Spieler &8- &7Seite &e" + page);
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Deko-Rand (wie bei PlayerSelectGui)
        ItemStack glass = item(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, glass);
        }

        // OfflinePlayers holen & sortieren
        List<OfflinePlayer> list = new ArrayList<>();
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op == null) continue;
            String name = op.getName();
            if (name == null || name.isBlank()) continue;
            list.add(op);
        }

        list.sort(Comparator.comparing(op -> op.getName().toLowerCase()));

        int maxPages = Math.max(1, (int) Math.ceil(list.size() / (double) PAGE_SIZE));
        if (page > maxPages) page = maxPages;

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, list.size());

        // Slots im Innenbereich
        int[] slots = {
                10,11,12,13,14,15,16,
                19,20,21,22,23,24,25,
                28,29,30,31,32,33,34,
                37,38,39,40,41,42,43
        };

        int idxSlot = 0;
        for (int i = start; i < end; i++) {
            OfflinePlayer target = list.get(i);
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            if (meta != null) {
                // Wichtig: OfflinePlayer geht auch (zeigt Skin, falls gecached)
                meta.setOwningPlayer(target);
                meta.setDisplayName(MessageUtil.color("&e" + target.getName()));
                meta.setLore(List.of(
                        MessageUtil.color("&7Klicken um diesen Spieler zu reporten"),
                        MessageUtil.color("&8(UUID wird gespeichert)")
                ));
                head.setItemMeta(meta);
            }

            inv.setItem(slots[idxSlot], head);
            idxSlot++;
            if (idxSlot >= slots.length) break;
        }

        // Navigation
        inv.setItem(49, item(Material.BARRIER, "&cZurück", List.of("&7Zurück zur Online-Auswahl")));

        if (page > 1) {
            inv.setItem(48, item(Material.ARROW, "&eZurück", List.of("&7Seite " + (page - 1))));
        }
        if (page < maxPages) {
            inv.setItem(50, item(Material.ARROW, "&eWeiter", List.of("&7Seite " + (page + 1))));
        }

        inv.setItem(53, item(Material.SUNFLOWER, "&aRefresh", List.of("&7Liste neu laden")));

        return inv;
    }

    private static ItemStack item(Material mat, String name, List<String> lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta im = it.getItemMeta();
        if (im != null) {
            im.setDisplayName(MessageUtil.color(name));
            if (lore != null) im.setLore(lore.stream().map(MessageUtil::color).toList());
            it.setItemMeta(im);
        }
        return it;
    }
}
