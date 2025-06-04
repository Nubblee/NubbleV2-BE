package dev.biddan.nubblev2.study.announcement.service.dto;

import java.time.LocalDate;
import lombok.Builder;

public class StudyAnnouncementCommand {

    @Builder
    public record Create(
            String title,
            String description,
            Integer recruitCapacity,
            LocalDate endDate,
            String applicationFormContent
    ) {

    }
}
