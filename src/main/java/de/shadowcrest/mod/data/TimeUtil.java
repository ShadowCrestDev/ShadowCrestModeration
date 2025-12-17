package de.shadowcrest.mod.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
    private static final Pattern P = Pattern.compile("^(\\d+)([smhd])$"); // seconds/minutes/hours/days

    public static long parseDurationToMillis(String input) {
        Matcher m = P.matcher(input.toLowerCase());
        if (!m.matches()) return -1;

        long n = Long.parseLong(m.group(1));
        String unit = m.group(2);

        return switch (unit) {
            case "s" -> n * 1000L;
            case "m" -> n * 60_000L;
            case "h" -> n * 3_600_000L;
            case "d" -> n * 86_400_000L;
            default -> -1;
        };
    }
}
