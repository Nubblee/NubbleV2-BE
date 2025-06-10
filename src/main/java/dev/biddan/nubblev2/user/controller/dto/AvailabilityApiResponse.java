package dev.biddan.nubblev2.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AvailabilityApiResponse(
        boolean isAvailable,
        String reason,
        String message
) {

    public static AvailabilityApiResponse available() {
        return new AvailabilityApiResponse(true, null, null);
    }

    public static AvailabilityApiResponse notAvailable(String reason, String message) {
        return new AvailabilityApiResponse(false, reason, message);
    }

}
