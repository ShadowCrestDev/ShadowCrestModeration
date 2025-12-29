package de.shadowcrest.mod.tickets.gui;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TicketGuiListener implements Listener {

    private final ShadowCrestMod plugin;

    public TicketGuiListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getView() == null || e.getView().getTitle() == null) return;

        String title = MessageUtil.color(plugin.getConfig().getString("messages.ticket_gui_title", "&5Ticket erstellen"));
        if (!e.getView().getTitle().equals(title)) return;

        e.setCancelled(true);

        ItemStack it = e.getCurrentItem();
        if (it == null || !it.hasItemMeta() || it.getItemMeta().getDisplayName() == null) return;

        String reason = MessageUtil.color(it.getItemMeta().getDisplayName()).replace("§d", "").replace("§5", "");
        p.closeInventory();

        plugin.getTicketManager().getSessions().put(p.getUniqueId(), new TicketSession(reason));
        p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_choose_player"));
    }
}
