package dev.biddan.nubblev2.study.applicationform.service.dto;

import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.study.applicationform.service.ApplicationFormService;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import java.time.LocalDateTime;
import lombok.Builder;

public class ApplicationFormInfo {

    @Builder
    public record Basic(
            Long id,
            String content,
            String status,
            LocalDateTime submittedAt,
            Long announcementId,
            UserInfo.Public applicant
    ) {

        public static Basic from(StudyApplicationForm savedForm) {
            return Basic.builder()
                    .id(savedForm.getId())
                    .content(savedForm.getContent().getValue())
                    .status(savedForm.getStatus().toString())
                    .submittedAt(savedForm.getSubmittedAt())
                    .announcementId(savedForm.getAnnouncement().getId())
                    .applicant(UserInfo.Public.from(savedForm.getApplicant()))
                    .build();
        }
    }
}
