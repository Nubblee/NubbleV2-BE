package dev.biddan.nubblev2.study.announcement.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

public class StudyAnnouncementCommand {

        @Builder
        public record Create(
                String title,
                String description,
                Integer recruitCapacity,
                LocalDateTime startDateTime,
                LocalDateTime endDateTime,
                String applicationFormContent
        ) {
        }
    }
