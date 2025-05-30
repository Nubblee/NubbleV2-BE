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

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            List<String> languages,

            String mainLanguage,

            List<String> difficultyLevels,

            List<String> problemPlatforms,

            String meetingType,

            String meetingRegion,

            List<String> mainMeetingDays
    ) {

        public StudyGroupCommand.Create toCreateCommand(Long creatorUserId) {
            return StudyGroupCommand.Create.builder()
                    .name(name)
                    .description(description)
                    .capacity(capacity)
                    .startDate(startDate)
                    .endDate(endDate)
                    .languages(languages)
                    .mainLanguage(mainLanguage)
                    .difficultyLevels(difficultyLevels)
                    .problemPlatforms(problemPlatforms)
                    .meetingType(meetingType)
                    .meetingRegion(meetingRegion)
                    .mainMeetingDays(mainMeetingDays)
                    .creatorId(creatorUserId)
                    .build();
        }

        public StudyGroupCommand.Update toUpdateCommand() {
            return StudyGroupCommand.Update.builder()
                    .name(name)
                    .description(description)
                    .capacity(capacity)
                    .startDate(startDate)
                    .endDate(endDate)
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
