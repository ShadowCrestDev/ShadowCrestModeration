package de.shadowcrest.mod.tickets;

public class TicketSession {
    public enum Step { PICK_PLAYER, PICK_INFO }

    private final String reason;
    private Step step = Step.PICK_PLAYER;

    private String targetName;
    private boolean targetNeverPlayed = false;
    private java.util.UUID targetUuid;

    public TicketSession(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public Step getStep() { return step; }
    public void setStep(Step step) { this.step = step; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public boolean isTargetNeverPlayed() { return targetNeverPlayed; }
    public void setTargetNeverPlayed(boolean targetNeverPlayed) { this.targetNeverPlayed = targetNeverPlayed; }

    public java.util.UUID getTargetUuid() { return targetUuid; }
    public void setTargetUuid(java.util.UUID targetUuid) { this.targetUuid = targetUuid; }
}
