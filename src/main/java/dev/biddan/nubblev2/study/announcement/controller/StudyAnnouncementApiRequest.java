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
    }
}
