package de.shadowcrest.mod.tickets.listeners;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.tickets.Ticket;
import de.shadowcrest.mod.tickets.TicketSession;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
        Player p = e.getPlayer();
        TicketSession session = plugin.getTicketManager().getSessions().get(p.getUniqueId());
        if (session == null) return;

        e.setCancelled(true);

        String msg = e.getMessage().trim();

        if (msg.equalsIgnoreCase("abort")) {
            plugin.getTicketManager().getSessions().remove(p.getUniqueId());
            Bukkit.getScheduler().runTask(plugin, () -> p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_aborted")));
            return;
        }

        // Step 1: Player name
        if (session.getStep() == TicketSession.Step.PICK_PLAYER) {
            String targetName = msg;

            OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);
            boolean neverPlayed = (off == null) || (!off.hasPlayedBefore() && !off.isOnline());

            session.setTargetName(targetName);
            session.setTargetUuid(neverPlayed ? null : off.getUniqueId());
            session.setTargetNeverPlayed(neverPlayed);

            session.setStep(TicketSession.Step.PICK_INFO);

            Bukkit.getScheduler().runTask(plugin, () -> p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_choose_info")));
            return;
        }

        // Step 2: Info
        if (session.getStep() == TicketSession.Step.PICK_INFO) {
            String info = msg.equalsIgnoreCase("skip") ? "" : msg;

            Bukkit.getScheduler().runTask(plugin, () -> {
                // checks: enabled/cooldown/max open
                if (!plugin.getConfig().getBoolean("tickets.enabled", true)) {
                    p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_disabled"));
                    plugin.getTicketManager().getSessions().remove(p.getUniqueId());
                    return;
                }

                long remaining = plugin.getTicketManager().getCooldownRemainingSeconds(p.getUniqueId());
                if (remaining > 0) {
                    p.sendMessage(MessageUtil.format(plugin, "messages.ticket_cooldown", MessageUtil.ph("seconds", remaining)));
                    plugin.getTicketManager().getSessions().remove(p.getUniqueId());
                    return;
                }

                int maxOpen = plugin.getConfig().getInt("tickets.max_open_per_player", 3);
                int open = plugin.getTicketManager().getOpenCount(p.getUniqueId());
                if (open >= maxOpen) {
                    p.sendMessage(MessageUtil.format(plugin, "messages.ticket_max_open", MessageUtil.ph("max", maxOpen)));
                    plugin.getTicketManager().getSessions().remove(p.getUniqueId());
                    return;
                }

                String targetDisplay = session.isTargetNeverPlayed()
                        ? ("Unbekannt:" + session.getTargetName())
                        : session.getTargetName();

                Ticket t = plugin.getTicketManager().createTicket(
                        p.getUniqueId(), p.getName(),
                        session.getTargetUuid(),
                        targetDisplay,
                        session.getReason(),
                        info
                );

                plugin.getTicketManager().markCreatedNow(p.getUniqueId());
                plugin.getTicketManager().save();
                plugin.getTicketManager().getSessions().remove(p.getUniqueId());

                p.sendMessage(MessageUtil.format(plugin, "messages.ticket_created", MessageUtil.ph("id", t.getId())));

                // staff notify - auffällig
                String staffMsg = MessageUtil.format(plugin, "messages.staff_ticket_new",
                        MessageUtil.ph("id", t.getId(), "creator", t.getCreatorName(), "target", t.getTargetName(), "reason", t.getReason()));
                String hint = MessageUtil.msg(plugin, "messages.staff_ticket_hint");

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("shadowcrest.mod.ticket.notify")) {
                        staff.sendMessage(staffMsg);
                        staff.sendMessage(hint);
                        staff.playSound(staff.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                    }
                }

                plugin.getLogger().info("[TICKET] #" + t.getId() + " " + t.getCreatorName() + " -> " + t.getTargetName() + " (" + t.getReason() + ")");
            });
        }
    }
}
