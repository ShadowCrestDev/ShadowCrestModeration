package de.shadowcrest.mod.tickets.chat;

import de.shadowcrest.mod.ShadowCrestMod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TicketPrivateChatListener implements Listener {

    private final ShadowCrestMod plugin;
    private final TicketChatManager chat;

    public TicketPrivateChatListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
        this.chat = plugin.getTicketChatManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player sender = e.getPlayer();

        Integer ticketId = chat.getActiveChat(sender.getUniqueId());
        if (ticketId == null) return;

        TicketChatSession session = chat.getSession(ticketId);
        if (session == null || !session.isOpen() || !session.isParticipant(sender.getUniqueId())) {
            chat.disableChat(sender.getUniqueId());
            return;
        }

        e.setCancelled(true);
        String msg = e.getMessage().trim();

        Bukkit.getScheduler().runTask(plugin, () ->
                chat.sendTicketMessage(ticketId, sender, msg)
        );
    }
}
