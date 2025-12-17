package de.shadowcrest.mod.data;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    public static class WarnEntry {
        public long timestamp;
        public String reason;

        public WarnEntry(long timestamp, String reason) {
            this.timestamp = timestamp;
            this.reason = reason;
        }
    }

    private final String uuid;
    private int warns;
    private final List<WarnEntry> warnHistory = new ArrayList<>();

    public PlayerData(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() { return uuid; }

    public int getWarns() { return warns; }

    public void setWarns(int warns) {
        this.warns = warns;
    }

    public List<WarnEntry> getWarnHistory() { return warnHistory; }

    public void addWarn(String reason) {
        warns++;
        warnHistory.add(new WarnEntry(System.currentTimeMillis(), reason));
    }
    public void clearWarns() {
        this.warns = 0;
        this.warnHistory.clear();
    }

}
