package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
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

        String title = MessageUtil.color(plugin.getConfig().getString("messages.ticket_gui_title", "&8SCM &7Tickets"));
        if (e.getView().getTitle() == null || !e.getView().getTitle().equals(title)) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

        String name = e.getCurrentItem().getItemMeta().getDisplayName();
        if (name == null || name.isBlank()) return;

        String categoryPlain = MessageUtil.color(name).replace("§", ""); // nur als Text
        categoryPlain = categoryPlain.replaceAll("[^A-Za-zÄÖÜäöüß ]", "").trim(); // grob reinigen

        TicketSession session = new TicketSession();
        session.setStep(TicketSession.Step.TARGET);
        session.setCategory(categoryPlain);

        plugin.getTicketManager().getSessions().put(p.getUniqueId(), session);

        p.closeInventory();
        p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_enter_player"));
    }
}
