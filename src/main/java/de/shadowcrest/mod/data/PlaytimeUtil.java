package de.shadowcrest.mod.data;

import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

public final class PlaytimeUtil {

    private PlaytimeUtil() {}

    /** Gibt Playtime in Ticks zurück (20 Ticks = 1 Sekunde). -1 wenn nicht verfügbar. */
    public static long getPlayTicks(OfflinePlayer player) {
        try {
            if (player == null) return -1;
            // Bei Spielern die nie drauf waren: kann 0 oder Fehler sein, je nach Server
            return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        } catch (Throwable t) {
            return -1;
        }
    }

    public static String formatPlaytime(long ticks, String format) {
        if (ticks < 0) return "Unbekannt";

        long totalSeconds = ticks / 20L;

        long days = totalSeconds / 86400L;
        long hours = (totalSeconds % 86400L) / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;

        String f = (format == null || format.isBlank()) ? "{hours}h {minutes}m" : format;
        return f.replace("{days}", String.valueOf(days))
                .replace("{hours}", String.valueOf(hours))
                .replace("{minutes}", String.valueOf(minutes));
    }
}
