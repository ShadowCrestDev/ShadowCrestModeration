package de.shadowcrest.mod.tickets.chat;

import java.util.UUID;

public class TicketChatSession {

    private final int ticketId;
    private final UUID requesterUuid;
    private UUID supporterUuid;
    private boolean open = true;

    public TicketChatSession(int ticketId, UUID requesterUuid, UUID supporterUuid) {
        this.ticketId = ticketId;
        this.requesterUuid = requesterUuid;
        this.supporterUuid = supporterUuid;
    }

    public int getTicketId() { return ticketId; }
    public UUID getRequesterUuid() { return requesterUuid; }

    public UUID getSupporterUuid() { return supporterUuid; }
    public void setSupporterUuid(UUID supporterUuid) { this.supporterUuid = supporterUuid; }

    public boolean isOpen() { return open; }
    public void close() { this.open = false; }

    public boolean isParticipant(UUID uuid) {
        return uuid.equals(requesterUuid) || (supporterUuid != null && uuid.equals(supporterUuid));
    }
}
