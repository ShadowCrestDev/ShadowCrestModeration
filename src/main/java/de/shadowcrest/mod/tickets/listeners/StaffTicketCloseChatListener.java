package de.shadowcrest.mod.tickets.listeners;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.Ticket;
import de.shadowcrest.mod.tickets.gui.StaffTicketGui;
import de.shadowcrest.mod.tickets.gui.StaffTicketGuiListener;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class StaffTicketCloseChatListener implements Listener {

    private final ShadowCrestMod plugin;

    public StaffTicketCloseChatListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Integer ticketId = StaffTicketGuiListener.CLOSE_REASON.get(p.getUniqueId());
        if (ticketId == null) return;

        e.setCancelled(true);

        String msg = e.getMessage().trim();

        if (msg.equalsIgnoreCase("abbrechen") || msg.equalsIgnoreCase("cancel")) {
            StaffTicketGuiListener.CLOSE_REASON.remove(p.getUniqueId());
            Bukkit.getScheduler().runTask(plugin, () -> {
                p.sendMessage(MessageUtil.color("&7Abgebrochen."));
                p.openInventory(StaffTicketGui.build(plugin, p, 1));
            });
            return;
        }

        StaffTicketGuiListener.CLOSE_REASON.remove(p.getUniqueId());

        Bukkit.getScheduler().runTask(plugin, () -> {
            Ticket t = plugin.getTicketManager().getTicket(ticketId);
            if (t == null) {
                p.sendMessage(MessageUtil.color("&cTicket existiert nicht mehr."));
                p.openInventory(StaffTicketGui.build(plugin, p, 1));
                return;
            }

            t.close(p.getName(), msg);
            plugin.getTicketManager().save();

            p.sendMessage(MessageUtil.color("&aTicket #" + t.getId() + " wurde geschlossen."));
            p.openInventory(StaffTicketGui.build(plugin, p, 1));
        });
    }
}
