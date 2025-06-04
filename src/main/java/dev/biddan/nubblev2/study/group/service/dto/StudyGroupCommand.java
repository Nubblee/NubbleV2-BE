package dev.biddan.nubblev2.study.group.service.dto;

import java.util.List;
import lombok.Builder;

public class StudyGroupCommand {

    @Builder
    public record Create(
            String name,
            String description,
            Integer capacity,
            List<String> languages,
            String mainLanguage,
            List<String> difficultyLevels,
            List<String> problemPlatforms,
            String meetingType,
            String meetingRegion,
            List<String> mainMeetingDays
    ) {

    }
}
