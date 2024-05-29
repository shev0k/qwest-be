package com.qwest.backend.domain.util;

public class NotificationUtils {

    public static String formatMessage(String type, String... args) {
        return switch (type) {
            case "REQUEST_TO_HOST" -> "You have a new host request.";
            case "BECOME_HOST" -> "You have been approved as a host.";
            case "REJECT_HOST" -> "Your request to become a host has been rejected.";
            case "DEMOTE_TRAVELER" -> "You have been demoted to traveler.";
            case "STAY_REVIEW" -> String.format("%s left a review on your stay.", args[0]);
            case "RESERVATION" -> String.format("%s made a reservation: %s.", args[0], args[1]);
            case "CANCEL_RESERVATION" -> String.format("%s canceled a reservation: %s.", args[0], args[1]);
            default -> "You have a new notification.";
        };
    }
}
