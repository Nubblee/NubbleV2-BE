package dev.biddan.nubblev2.user.interest.servicce.dto;

import dev.biddan.nubblev2.user.interest.domain.UserInterest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class UserInterestInfo {

    @Builder
    public record Basic(
            Long userId,
            List<String> interestedLanguages,
            List<String> currentLevels,
            List<String> preferredPlatforms,
            LocalDateTime createdAt,
            LocalDateTime updatedAt

    ) {

        public static Basic of(UserInterest userInterest) {
            return Basic.builder()
                    .userId(userInterest.getUserId())
                    .interestedLanguages(userInterest.getInterestedLanguages().stream()
                            .map(Enum::name)
                            .toList())
                    .currentLevels(userInterest.getCurrentLevels().stream()
                            .map(Enum::name)
                            .toList())
                    .preferredPlatforms(userInterest.getPreferredPlatforms().stream()
                            .map(Enum::name)
                            .toList())
                    .createdAt(userInterest.getCreatedAt())
                    .updatedAt(userInterest.getUpdatedAt())
                    .build();
        }
    }
}
