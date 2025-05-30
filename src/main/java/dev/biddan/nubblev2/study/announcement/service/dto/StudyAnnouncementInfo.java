package dev.biddan.nubblev2.study.announcement.service.dto;

import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import java.time.LocalDateTime;
import lombok.Builder;

public class StudyAnnouncementInfo {

    @Builder
    public record Basic(
            Long id,
            Long studyGroupId,
            String title,
            String description,
            Integer recruitCapacity,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String status,
            String closedReason,
            LocalDateTime createdAt,
            LocalDateTime closedAt
    ) {

        public static Basic from(StudyAnnouncement announcement) {
            return Basic.builder()
                    .id(announcement.getId())
                    .studyGroupId(announcement.getStudyGroup().getId())
                    .title(announcement.getTitle().getValue())
                    .description(announcement.getDescription().getValue())
                    .recruitCapacity(announcement.getRecruitCapacity().getValue())
                    .startDateTime(announcement.getPeriod().getStartDateTime())
                    .endDateTime(announcement.getPeriod().getEndDateTime())
                    .status(announcement.getStatus().toString())
                    .closedReason(getClosedReason(announcement))
                    .createdAt(announcement.getCreatedAt())
                    .closedAt(announcement.getClosedAt())
                    .build();
        }
    }

    private static String getClosedReason(StudyAnnouncement announcement) {
        return announcement.getClosedReason() != null ? announcement.getClosedReason().toString() : null;
    }
}
