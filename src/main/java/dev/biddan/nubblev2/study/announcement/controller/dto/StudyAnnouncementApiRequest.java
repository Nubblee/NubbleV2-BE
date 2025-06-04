package dev.biddan.nubblev2.study.announcement.controller.dto;

import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementCommand;
import java.time.LocalDate;
import lombok.Builder;

public class StudyAnnouncementApiRequest {

    @Builder
    public record Create(
            Long studyGroupId,
            String title,
            String description,
            Integer recruitCapacity,
            LocalDate endDate,
            String applicationFormContent
    ) {

        public StudyAnnouncementCommand.Create toCreateCommand() {
            return StudyAnnouncementCommand.Create.builder()
                    .title(title)
                    .description(description)
                    .recruitCapacity(recruitCapacity)
                    .endDate(endDate)
                    .applicationFormContent(applicationFormContent)
                    .build();
        }
    }
}
