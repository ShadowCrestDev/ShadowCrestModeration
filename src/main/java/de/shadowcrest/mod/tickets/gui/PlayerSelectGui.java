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

    public static Inventory build(ShadowCrestMod plugin, Player viewer) {
        String title = MessageUtil.color("&8SCM &7Spieler auswählen");
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Deko-Rand (optional, aber sieht besser aus)
        ItemStack glass = item(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) inv.setItem(i, glass);
        }

        int slot = 10;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (slot >= 44) break; // Platz begrenzt (innenbereich)

            // überspringe Rand-Slots automatisch
            while (slot % 9 == 0 || slot % 9 == 8) slot++;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            if (meta != null) {
                meta.setOwningPlayer(target);
                meta.setDisplayName(MessageUtil.color("&e" + target.getName()));
                meta.setLore(List.of(
                        MessageUtil.color("&7Klicken um diesen Spieler zu reporten")
                ));
                head.setItemMeta(meta);
            }


            inv.setItem(slot, head);
            slot++;
        }

        // Zurück-Button
        inv.setItem(49, item(Material.BARRIER, "&cZurück", List.of("&7Zurück zur Ticket-Auswahl")));

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
