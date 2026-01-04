package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TicketGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public TicketGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        // ✅ Titel aus Language
        String title = plugin.getLang().get("messages.ticket_gui_title");
        if (e.getView().getTitle() == null || !e.getView().getTitle().equals(title)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        if (name == null || name.isBlank()) return;

        // ✅ Farbcodes korrekt entfernen
        String categoryPlain = ChatColor.stripColor(MessageUtil.color(name));
        if (categoryPlain == null) return;

        // optional: nur Buchstaben + Leerzeichen
        categoryPlain = categoryPlain.replaceAll("[^A-Za-zÄÖÜäöüß ]", "").trim();

        TicketSession session = new TicketSession();
        session.setCategory(categoryPlain);

        session.setStep(TicketSession.Step.TARGET);
        plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);

        p.closeInventory();
        p.openInventory(PlayerSelectGui.build(plugin, p));
    }
}
