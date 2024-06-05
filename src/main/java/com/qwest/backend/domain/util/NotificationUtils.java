package com.qwest.backend.domain.util;

public class NotificationUtils {

    public static String formatMessage(String type, String... args) {
        return switch (type) {
            case "REQUEST_TO_HOST" -> "Has applied to become a host.";
            case "BECOME_HOST" -> "Has approved you as a host.";
            case "REJECT_HOST" -> "Rejected your request to become a host.";
            case "DEMOTE_TRAVELER" -> "Has demoted you to a normal user.";
            case "STAY_REVIEW" -> "Left a review on your stay.";
            case "RESERVATION" -> "Has reserved your stay";
            case "CANCEL_RESERVATION" -> "Has canceled a reservation on your stay";
            default -> "You have a new notification.";
        };
    }
}
