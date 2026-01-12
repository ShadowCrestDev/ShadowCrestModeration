package de.shadowcrest.mod.tickets;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;


public class Ticket {

    private final int id;
    private final long createdAt;

    private final UUID creatorUuid;
    private final String creatorName;

    private final UUID targetUuid; // kann null sein (offline/unknown)
    private final String targetName;

    private final String reason; // Kategorie
    private final String info;   // Zusatzinfo

    // Status
    private TicketStatus status = TicketStatus.OPEN;

    // Claim-Daten
    private UUID claimedByUuid;
    private String claimedByName;
    private long claimedAt;

    // Close-Daten
    private long closedAt = 0L;
    private String closedByName = null;
    private String closeReason = null;

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

    // Claim
    public boolean isClaimed() { return status == TicketStatus.CLAIMED; }
    public UUID getClaimedByUuid() { return claimedByUuid; }
    public String getClaimedByName() { return claimedByName == null ? "Unknown" : claimedByName; }
    public long getClaimedAt() { return claimedAt; }

    public void claim(UUID staffUuid, String staffName) {
        this.claimedByUuid = staffUuid;
        this.claimedByName = staffName;
        this.claimedAt = System.currentTimeMillis();
        this.status = TicketStatus.CLAIMED;
    }

    public void setClaimedByUuid(UUID claimedByUuid) { this.claimedByUuid = claimedByUuid; }
    public void setClaimedByName(String claimedByName) { this.claimedByName = claimedByName; }
    public void setClaimedAt(long claimedAt) { this.claimedAt = claimedAt; }

    // Close
    public boolean isClosed() { return status == TicketStatus.CLOSED; }
    public long getClosedAt() { return closedAt; }
    public String getClosedByName() { return closedByName; }
    public String getCloseReason() { return closeReason; }

    public void close(String staffName, String reason) {
        this.status = TicketStatus.CLOSED;
        this.closedAt = System.currentTimeMillis();
        this.closedByName = staffName;
        this.closeReason = (reason == null ? "" : reason);
    }

    public void setClosedAt(long closedAt) { this.closedAt = closedAt; }
    public void setClosedByName(String closedByName) { this.closedByName = closedByName; }
    public void setCloseReason(String closeReason) { this.closeReason = closeReason; }
    // ✅ NEU: Aktionen die aus dem Ticket heraus gemacht wurden
    private final List<String> linkedActions = new ArrayList<>();

    public List<String> getLinkedActions() {
        return linkedActions;
    }

    public void addLinkedAction(String line) {
        if (line == null || line.isBlank()) return;
        linkedActions.add(line);
    }

}
