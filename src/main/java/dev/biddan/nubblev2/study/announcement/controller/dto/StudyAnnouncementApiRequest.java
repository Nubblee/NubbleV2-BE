package dev.biddan.nubblev2.study.announcement.controller.dto;

import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementCommand;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class StudyAnnouncementApiRequest {

    @Builder
    public record Create(
            Long studyGroupId,
            String title,
            String description,
            Integer recruitCapacity,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String applicationFormContent
    ) {

        public StudyAnnouncementCommand.Create toCreateCommand() {
            return StudyAnnouncementCommand.Create.builder()
                    .title(title)
                    .description(description)
                    .recruitCapacity(recruitCapacity)
                    .startDateTime(startDateTime)
                    .endDateTime(endDateTime)
                    .applicationFormContent(applicationFormContent)
                    .build();
        }
    }
}
