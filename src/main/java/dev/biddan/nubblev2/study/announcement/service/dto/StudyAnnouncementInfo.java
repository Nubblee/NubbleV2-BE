package dev.biddan.nubblev2.study.announcement.service.dto;

import dev.biddan.nubblev2.study.announcement.domain.AnnouncementApplicationForm;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.ClosedReason;
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
            LocalDateTime closedAt,
            String applicationForm
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
                    .applicationForm(getApplicationFormContent(announcement))
                    .build();
        }
    }

    private static String getClosedReason(StudyAnnouncement announcement) {
        ClosedReason closedReason = announcement.getClosedReason();
        return closedReason != null ? closedReason.toString() : null;
    }

    private static String getApplicationFormContent(StudyAnnouncement announcement) {
        AnnouncementApplicationForm form = announcement.getAnnouncementApplicationForm();
        return form != null ? form.getContent() : null;
    }
}
