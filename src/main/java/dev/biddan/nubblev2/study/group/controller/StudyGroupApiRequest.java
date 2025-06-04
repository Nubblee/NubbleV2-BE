package dev.biddan.nubblev2.study.group.controller;

import dev.biddan.nubblev2.study.group.service.dto.StudyGroupCommand;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

public class StudyGroupApiRequest {

    @Builder(toBuilder = true)
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

        public StudyGroupCommand.Create toCreateCommand() {
            return StudyGroupCommand.Create.builder()
                    .name(name)
                    .description(description)
                    .capacity(capacity)
                    .languages(languages)
                    .mainLanguage(mainLanguage)
                    .difficultyLevels(difficultyLevels)
                    .problemPlatforms(problemPlatforms)
                    .meetingType(meetingType)
                    .meetingRegion(meetingRegion)
                    .mainMeetingDays(mainMeetingDays)
                    .build();
        }
    }
}
