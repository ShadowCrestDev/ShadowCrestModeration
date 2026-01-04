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

import java.util.List;
import java.util.Map;

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

        // Cancel-Wörter aus Language
        List<String> cancelWords = plugin.getLang().getStringList("messages.staff_ticket_close_cancel_words");
        boolean isCancel = false;
        if (cancelWords != null) {
            for (String w : cancelWords) {
                if (w != null && !w.isBlank() && msg.equalsIgnoreCase(w.trim())) {
                    isCancel = true;
                    break;
                }
            }
        }

        if (isCancel) {
            StaffTicketGuiListener.CLOSE_REASON.remove(p.getUniqueId());
            Bukkit.getScheduler().runTask(plugin, () -> {
                p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_close_cancelled"));
                p.openInventory(StaffTicketGui.build(plugin, p, 1));
            });
            return;
        }

        StaffTicketGuiListener.CLOSE_REASON.remove(p.getUniqueId());

        Bukkit.getScheduler().runTask(plugin, () -> {
            Ticket t = plugin.getTicketManager().getTicket(ticketId);
            if (t == null) {
                p.sendMessage(MessageUtil.msg(plugin, "messages.staff_ticket_not_exists"));
                p.openInventory(StaffTicketGui.build(plugin, p, 1));
                return;
            }

            t.close(p.getName(), msg);
            plugin.getTicketManager().save();

            p.sendMessage(MessageUtil.format(
                    plugin,
                    "messages.staff_ticket_closed_done",
                    MessageUtil.ph("id", t.getId())
            ));
            p.openInventory(StaffTicketGui.build(plugin, p, 1));
        });
    }
}
