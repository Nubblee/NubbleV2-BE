package dev.biddan.nubblev2.user.interest.controller.dto;

import java.util.List;
import lombok.Builder;

public class UserInterestApiRequest {

    @Builder
    public record Set(
            List<String> interestedLanguages,
            List<String> currentLevels,
            List<String> preferredPlatforms
    ) {

    }
}
