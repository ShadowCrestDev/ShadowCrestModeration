package de.shadowcrest.mod.tickets.listeners;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class TicketChatListener implements Listener {

    private final ShadowCrestMod plugin;

    public TicketChatListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        TicketSession session = plugin.getTicketManager().getSessions().get(uuid);
        if (session == null) return;

        e.setCancelled(true);

        String msg = e.getMessage().trim();
        if (msg.equalsIgnoreCase("abbrechen") || msg.equalsIgnoreCase("cancel")) {
            plugin.getTicketManager().getSessions().remove(uuid);
            e.getPlayer().sendMessage(MessageUtil.msg(plugin, "messages.ticket_player_cancel"));
            return;
        }

        // Schritt 1: Zielspieler
        if (session.getStep() == TicketSession.Step.TARGET) {
            String targetName = msg;

            OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);
            UUID targetUuid = null;

            if (off != null && (off.hasPlayedBefore() || off.isOnline())) {
                targetUuid = off.getUniqueId();
            } else {
                e.getPlayer().sendMessage(MessageUtil.format(plugin, "messages.ticket_player_unknown",
                        MessageUtil.ph("player", targetName)));
            }

            session.setTargetUuid(targetUuid);
            session.setTargetName(targetName);
            session.setStep(TicketSession.Step.INFO);

            e.getPlayer().sendMessage(MessageUtil.msg(plugin, "messages.ticket_enter_info"));
            return;
        }

        // Schritt 2: Info (optional)
        if (session.getStep() == TicketSession.Step.INFO) {
            String info = msg.equals("-") ? "" : msg;

            // Max offene Tickets
            int maxOpen = plugin.getConfig().getInt("tickets.max_open_per_player", 3);
            int currentOpen = plugin.getTicketManager().getOpenCount(uuid);
            if (currentOpen >= maxOpen) {
                e.getPlayer().sendMessage(MessageUtil.format(plugin, "messages.ticket_max_open",
                        MessageUtil.ph("max", maxOpen)));
                plugin.getTicketManager().getSessions().remove(uuid);
                return;
            }

            // Cooldown
            long remaining = plugin.getTicketManager().getCooldownRemainingSeconds(uuid);
            if (remaining > 0) {
                e.getPlayer().sendMessage(MessageUtil.format(plugin, "messages.ticket_cooldown",
                        MessageUtil.ph("seconds", remaining)));
                return;
            }

            var t = plugin.getTicketManager().createTicket(
                    uuid,
                    e.getPlayer().getName(),
                    session.getTargetUuid(),
                    session.getTargetName(),
                    session.getCategory(),
                    info
            );

            e.getPlayer().sendMessage(MessageUtil.format(plugin, "messages.ticket_created",
                    MessageUtil.ph("id", t.getId(), "category", session.getCategory(), "player", session.getTargetName())));

            plugin.getTicketManager().getSessions().remove(uuid);
        }
    }
}
