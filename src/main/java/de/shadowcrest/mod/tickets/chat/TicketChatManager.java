package de.shadowcrest.mod.tickets.chat;

import de.shadowcrest.mod.ShadowCrestMod;
import de.shadowcrest.mod.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TicketChatManager {

    private final ShadowCrestMod plugin;

    private final Map<Integer, TicketChatSession> sessionsById = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> activeChatByPlayer = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> lastTicketByPlayer = new ConcurrentHashMap<>();

    public TicketChatManager(ShadowCrestMod plugin) {
        this.plugin = plugin;
    }

    // Wird beim GUI-Claim aufgerufen
    public void startSession(int ticketId, UUID requester, UUID supporter) {
        sessionsById.put(ticketId, new TicketChatSession(ticketId, requester, supporter));
        lastTicketByPlayer.put(requester, ticketId);
        lastTicketByPlayer.put(supporter, ticketId);
    }

    public TicketChatSession getSession(int ticketId) { return sessionsById.get(ticketId); }
    public Integer getActiveChat(UUID player) { return activeChatByPlayer.get(player); }
    public Integer getLastTicket(UUID player) { return lastTicketByPlayer.get(player); }

    public void setLastTicket(UUID player, int ticketId) { lastTicketByPlayer.put(player, ticketId); }

    public boolean enableChat(UUID player, int ticketId) {
        TicketChatSession s = sessionsById.get(ticketId);
        if (s == null || !s.isOpen()) return false;
        if (!s.isParticipant(player)) return false;

        activeChatByPlayer.put(player, ticketId);
        lastTicketByPlayer.put(player, ticketId);
        return true;
    }

    public void disableChat(UUID player) {
        activeChatByPlayer.remove(player);
    }

    public void closeSession(int ticketId) {
        TicketChatSession s = sessionsById.get(ticketId);
        if (s != null) {
            s.close();
            activeChatByPlayer.remove(s.getRequesterUuid());
            if (s.getSupporterUuid() != null) activeChatByPlayer.remove(s.getSupporterUuid());
        }
    }

    public void sendClaimInfo(int ticketId, Player supporter, Player requester) {
        supporter.sendMessage(MessageUtil.format(plugin, "messages.ticket_chat_claimed_supporter",
                MessageUtil.ph("id", ticketId)));

        if (requester != null) {
            requester.sendMessage(MessageUtil.format(plugin, "messages.ticket_chat_claimed_requester",
                    MessageUtil.ph("id", ticketId, "supporter", supporter.getName())));
        }
    }

    public void sendModeEnabled(Player p, int ticketId) {
        p.sendMessage(MessageUtil.format(plugin, "messages.ticket_chat_mode_enabled",
                MessageUtil.ph("id", ticketId)));
    }

    public void sendModeDisabled(Player p) {
        p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_chat_mode_disabled"));
    }

    public void sendModeEnableFailed(Player p) {
        p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_chat_mode_enable_failed"));
    }

    public void sendNoLastTicket(Player p) {
        p.sendMessage(MessageUtil.msg(plugin, "messages.ticket_chat_no_last_ticket"));
    }

    public void sendSessionClosed(Player p, int ticketId) {
        p.sendMessage(MessageUtil.format(plugin, "messages.ticket_chat_session_closed",
                MessageUtil.ph("id", ticketId)));
    }

    public void sendTicketMessage(int ticketId, Player sender, String message) {
        TicketChatSession s = sessionsById.get(ticketId);
        if (s == null || !s.isOpen()) return;
        if (!s.isParticipant(sender.getUniqueId())) return;

        Player requester = Bukkit.getPlayer(s.getRequesterUuid());
        Player supporter = s.getSupporterUuid() != null ? Bukkit.getPlayer(s.getSupporterUuid()) : null;

        String line = MessageUtil.format(plugin, "messages.ticket_chat_format",
                MessageUtil.ph("id", ticketId, "sender", sender.getName(), "message", message));

        if (requester != null) requester.sendMessage(line);
        if (supporter != null) supporter.sendMessage(line);
    }
}
