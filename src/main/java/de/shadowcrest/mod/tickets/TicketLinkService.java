package de.shadowcrest.mod.tickets;

import de.shadowcrest.mod.ShadowCrestMod;

import java.util.Map;

public class TicketLinkService {

    private final ShadowCrestMod plugin;

    public TicketLinkService(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    public void logActionIfLinked(java.util.UUID staffUuid,
                                  String staffName,
                                  String action,
                                  String targetName,
                                  String duration,
                                  String reason) {

        Integer ticketId = plugin.getTicketContext().get(staffUuid);
        if (ticketId == null) return;

        Ticket t = plugin.getTicketManager().getTicket(ticketId);
        if (t == null) return;

        // nur link wenn Ticket target passt
        if (t.getTargetName() == null || t.getTargetName().isBlank()) return;
        if (targetName == null || targetName.isBlank()) return;

        if (!t.getTargetName().equalsIgnoreCase(targetName)) return;

        String durText = (duration == null || duration.isBlank()) ? "-" : duration;
        String rsnText = (reason == null || reason.isBlank()) ? "-" : reason;

        // Eintrag in Ticket speichern (ohne Farbcodes)
        String line = plugin.getLang().get("messages.ticket_linked_action_line", Map.of(
                "action", action.toUpperCase(),
                "staff", staffName,
                "player", targetName,
                "duration", durText,
                "reason", rsnText,
                "id", String.valueOf(t.getId())
        ));

        t.addLinkedAction(line);
        plugin.getTicketManager().save();

        // optional: Discord extra (Ticket-Actions)
        if (plugin.getConfig().getBoolean("discord.enabled", false)
                && plugin.getConfig().getBoolean("discord.events.tickets.actions", true)) {

            String text = plugin.getLang().get("messages.discord.ticket_action", Map.of(
                    "id", String.valueOf(t.getId()),
                    "staff", staffName,
                    "action", action.toUpperCase(),
                    "player", targetName,
                    "duration", durText,
                    "reason", rsnText
            ));

            plugin.getDiscord().sendPlainAsync(text);
        }
    }
}
