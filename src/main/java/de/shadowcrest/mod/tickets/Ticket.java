package de.shadowcrest.mod.tickets;

import java.util.UUID;

public class Ticket {
    private final int id;
    private final long createdAt;
    private final UUID creatorUuid;
    private final String creatorName;

    private final UUID targetUuid; // kann null sein
    private final String targetName; // immer gesetzt (auch wenn "Unbekannt:<name>")

    private final String reason;
    private final String info; // kann leer sein

    private TicketStatus status = TicketStatus.OPEN;

    private UUID claimedByUuid;
    private String claimedByName;
    private long claimedAt;

    public Ticket(int id, long createdAt, UUID creatorUuid, String creatorName,
                  UUID targetUuid, String targetName, String reason, String info) {
        this.id = id;
        this.createdAt = createdAt;
        this.creatorUuid = creatorUuid;
        this.creatorName = creatorName;
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.reason = reason;
        this.info = info == null ? "" : info;
    }

    public int getId() { return id; }
    public long getCreatedAt() { return createdAt; }
    public UUID getCreatorUuid() { return creatorUuid; }
    public String getCreatorName() { return creatorName; }
    public UUID getTargetUuid() { return targetUuid; }
    public String getTargetName() { return targetName; }
    public String getReason() { return reason; }
    public String getInfo() { return info; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public UUID getClaimedByUuid() { return claimedByUuid; }
    public String getClaimedByName() { return claimedByName; }
    public long getClaimedAt() { return claimedAt; }

    public void claim(UUID staffUuid, String staffName) {
        this.status = TicketStatus.CLAIMED;
        this.claimedByUuid = staffUuid;
        this.claimedByName = staffName;
        this.claimedAt = System.currentTimeMillis();
    }
}
