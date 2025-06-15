package dev.biddan.nubblev2.study.applicationform.service.dto;

import dev.biddan.nubblev2.study.applicationform.domain.ApplicationFormReview;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.study.applicationform.repository.ApplicationFormBlazeRepository.ApplicationFormPageResult;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import dev.biddan.nubblev2.user.service.dto.UserInfo.Public;
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
            UserInfo.Public applicant,
            Long reviewerId,
            LocalDateTime reviewedAt
    ) {

        public static Basic of(StudyApplicationForm form) {
            BasicBuilder builder = Basic.builder()
                    .id(form.getId())
                    .content(form.getContent().getValue())
                    .status(form.getStatus().toString())
                    .submittedAt(form.getSubmittedAt())
                    .announcementId(form.getAnnouncement().getId())
                    .applicant(Public.from(form.getApplicant()));

            if (form.getReview() != null) {
                ApplicationFormReview review = form.getReview();

                builder.reviewerId(review.getReviewer().getId())
                        .reviewedAt(review.getReviewedAt());
            }

            return builder.build();
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
