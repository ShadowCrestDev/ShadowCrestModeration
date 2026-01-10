package de.shadowcrest.mod.mute;

import java.util.UUID;

public class MutedPlayer {

    private final UUID uuid;
    private String name;

    /**
     * mutedUntil:
     * -1 = permanent
     * else epoch millis
     */
    private long mutedUntil;

    private String reason;
    private String mutedBy;

    public MutedPlayer(UUID uuid, String name, long mutedUntil, String reason, String mutedBy) {
        this.uuid = uuid;
        this.name = name;
        this.mutedUntil = mutedUntil;
        this.reason = reason;
        this.mutedBy = mutedBy;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getMutedUntil() { return mutedUntil; }
    public void setMutedUntil(long mutedUntil) { this.mutedUntil = mutedUntil; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getMutedBy() { return mutedBy; }
    public void setMutedBy(String mutedBy) { this.mutedBy = mutedBy; }

    public boolean isPermanent() {
        return mutedUntil < 0;
    }
}
