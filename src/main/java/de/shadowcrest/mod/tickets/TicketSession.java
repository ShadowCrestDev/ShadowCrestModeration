package de.shadowcrest.mod.tickets;

import java.util.UUID;

public class TicketSession {

    public enum Step {
        CATEGORY,
        TARGET,
        INFO
    }

    private Step step = Step.CATEGORY;

    private String category;
    private String targetName;
    private UUID targetUuid; // kann null sein (nie gespielt)
    private String info;

    public Step getStep() { return step; }
    public void setStep(Step step) { this.step = step; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public UUID getTargetUuid() { return targetUuid; }
    public void setTargetUuid(UUID targetUuid) { this.targetUuid = targetUuid; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }
}
