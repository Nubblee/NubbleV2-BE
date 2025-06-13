package dev.biddan.nubblev2.user.interest.controller.dto;

import dev.biddan.nubblev2.user.interest.servicce.dto.UserInterestCommand;
import java.util.List;
import lombok.Builder;

public class UserInterestApiRequest {

    @Builder
    public record Set(
            List<String> interestedLanguages,
            List<String> currentLevels,
            List<String> preferredPlatforms
    ) {


        public UserInterestCommand.Set toCommand() {
            return UserInterestCommand.Set.builder()
                    .interestedLanguages(interestedLanguages)
                    .currentLevels(currentLevels)
                    .preferredPlatforms(preferredPlatforms)
                    .build();
        }
    }
}
