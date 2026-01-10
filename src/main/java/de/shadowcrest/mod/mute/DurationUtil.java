package de.shadowcrest.mod.mute;

import java.util.Locale;

public final class DurationUtil {

    private DurationUtil() {}

    /**
     * Supports: 10s, 30m, 2h, 1d
     * Returns millis or null if invalid.
     */
    public static Long parseToMillis(String input) {
        if (input == null) return null;
        input = input.trim().toLowerCase(Locale.ROOT);
        if (input.isEmpty()) return null;

        // digits + unit
        if (input.length() < 2) return null;

        char unit = input.charAt(input.length() - 1);
        String numPart = input.substring(0, input.length() - 1);

        long value;
        try {
            value = Long.parseLong(numPart);
        } catch (Exception ignored) {
            return null;
        }

        if (value <= 0) return null;

        return switch (unit) {
            case 's' -> value * 1000L;
            case 'm' -> value * 60_000L;
            case 'h' -> value * 3_600_000L;
            case 'd' -> value * 86_400_000L;
            default -> null;
        };
    }

    public static String formatRemaining(long millis) {
        if (millis <= 0) return "0s";

        long seconds = millis / 1000L;
        long days = seconds / 86400; seconds %= 86400;
        long hours = seconds / 3600; seconds %= 3600;
        long minutes = seconds / 60; seconds %= 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 && days == 0) sb.append(seconds).append("s"); // only show seconds if not huge
        return sb.toString().trim();
    }
}
