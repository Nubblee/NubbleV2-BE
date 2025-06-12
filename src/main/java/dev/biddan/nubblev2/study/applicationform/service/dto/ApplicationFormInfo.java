package dev.biddan.nubblev2.study.applicationform.service.dto;

import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.study.applicationform.repository.ApplicationFormBlazeRepository.ApplicationFormPageResult;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import java.time.LocalDateTime;
import java.util.List;
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

        public static Basic of(StudyApplicationForm savedForm) {
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

    @Builder
    public record PageList(
            List<Basic> forms,
            boolean hasNext,
            Long lastId,
            LocalDateTime lastSubmittedAt
    ) {

        public static PageList of(ApplicationFormPageResult result) {
            List<Basic> list = result.studyApplicationForms().stream()
                    .map(Basic::of)
                    .toList();

            return PageList.builder()
                    .forms(list)
                    .hasNext(result.hasNext())
                    .lastId(result.lastId())
                    .lastSubmittedAt(result.lastSubmittedAt())
                    .build();
        }
    }
}
