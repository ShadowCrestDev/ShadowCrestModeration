package de.shadowcrest.mod.tickets.listeners;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TicketChatListener implements Listener {

    private final ShadowCrestMod plugin;

    public TicketChatListener(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        var uuid = e.getPlayer().getUniqueId();
        var sessions = plugin.getTicketManager().getSessions();
        TicketSession session = sessions.get(uuid);
        if (session == null) return;

        e.setCancelled(true);

        String msg = e.getMessage().trim();

        // cancel
        if (msg.equalsIgnoreCase("abbrechen") || msg.equalsIgnoreCase("cancel")) {
            sessions.remove(uuid);
            Bukkit.getScheduler().runTask(plugin, () ->
                    e.getPlayer().sendMessage(MessageUtil.msg(plugin, "messages.ticket_player_cancel")));
            return;
        }

        // STEP: TARGET
        if (session.getStep() == TicketSession.Step.TARGET) {
            String targetName = msg;

            OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);

            session.setTargetName(targetName);

            // wenn nie gespielt -> uuid kann trotzdem existieren, wir behandeln es als "unknown"
            boolean unknown = (off == null || (!off.hasPlayedBefore() && off.getName() == null));
            session.setTargetUuid(unknown ? null : off.getUniqueId());

            sessions.put(uuid, session);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (unknown) {
                    e.getPlayer().sendMessage(MessageUtil.format(plugin, "messages.ticket_player_unknown",
                            MessageUtil.ph("player", targetName)));
                }
                e.getPlayer().sendMessage(MessageUtil.msg(plugin, "messages.ticket_enter_info"));
            });

            session.setStep(TicketSession.Step.INFO);
            return;
        }

        // STEP: INFO
        if (session.getStep() == TicketSession.Step.INFO) {
            String info = msg.equals("-") ? "" : msg;
            session.setInfo(info);

            sessions.remove(uuid);

            // Ticket erstellen (sync)
            Bukkit.getScheduler().runTask(plugin, () -> {
                var creator = e.getPlayer();
                var t = plugin.getTicketManager().createTicket(
                        creator.getUniqueId(),
                        creator.getName(),
                        session.getTargetUuid(),
                        session.getTargetName(),
                        session.getCategory(),
                        session.getInfo()
                );

                creator.sendMessage(MessageUtil.format(plugin, "messages.ticket_created",
                        MessageUtil.ph("id", t.getId(), "category", session.getCategory(), "player", session.getTargetName())));
            });
        }
    }
}
