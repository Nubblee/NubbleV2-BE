package dev.biddan.nubblev2.user.interest;

import dev.biddan.nubblev2.user.interest.controller.dto.UserInterestApiRequest.Set;
import java.util.List;

public class UserInterestRequestFixture {

    public static Set generateValidSetRequest() {
        return Set.builder()
                .interestedLanguages(List.of("JAVA", "PYTHON"))
                .currentLevels(List.of("LV2", "LV3"))
                .preferredPlatforms(List.of("PROGRAMMERS", "BAEKJOON"))
                .build();
    }
}
