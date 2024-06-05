package com.qwest.backend.domain.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimestampUtils {

    public static String formatTimestamp(LocalDateTime timestamp) {
        Duration duration = Duration.between(timestamp, LocalDateTime.now());
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);

        if (absSeconds < 60) {
            return absSeconds + " seconds ago";
        } else if (absSeconds < 3600) {
            long minutes = absSeconds / 60;
            return minutes + " minutes ago";
        } else if (absSeconds < 86400) {
            long hours = absSeconds / 3600;
            return hours + " hours ago";
        } else {
            long days = absSeconds / 86400;
            return days + " days ago";
        }
    }
}