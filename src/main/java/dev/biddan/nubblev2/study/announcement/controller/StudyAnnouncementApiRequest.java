package dev.biddan.nubblev2.study.announcement.controller;

import java.time.LocalDateTime;
import lombok.Builder;

public class StudyAnnouncementApiRequest {

    @Builder
    public record Create(
            Long studyGroupId,
            String title,
            String description,
            Integer recruitCapacity,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {

        public StudyAnnouncementCommand.Create toCreateCommand() {
            return StudyAnnouncementCommand.Create.builder()
                    .title(title)
                    .description(description)
                    .recruitCapacity(recruitCapacity)
                    .startDateTime(startDateTime)
                    .endDateTime(endDateTime)
                    .build();
        }
    }

    public class StudyAnnouncementCommand {

        @Builder
        public record Create(
                String title,
                String description,
                Integer recruitCapacity,
                LocalDateTime startDateTime,
                LocalDateTime endDateTime
        ) {
        }
    }
}
