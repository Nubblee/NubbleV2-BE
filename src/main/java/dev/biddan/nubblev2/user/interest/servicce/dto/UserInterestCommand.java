package dev.biddan.nubblev2.user.interest.servicce.dto;

import java.util.List;
import lombok.Builder;

public class UserInterestCommand {

    @Builder
    public record Set(
            List<String> interestedLanguages,
            List<String> currentLevels,
            List<String> preferredPlatforms
    ) {

    }
}
