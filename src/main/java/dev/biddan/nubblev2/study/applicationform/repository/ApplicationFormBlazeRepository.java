package dev.biddan.nubblev2.study.applicationform.repository;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm.ApplicationFormStatus;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApplicationFormBlazeRepository {

    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    public ApplicationFormPageResult findApplicationForms(
            Long announcementId,
            Long lastId,
            LocalDateTime lastSubmittedAt,
            ApplicationFormStatus status,
            int pageSize
    ) {

        CriteriaBuilder<StudyApplicationForm> baseQuery = cbf
                .create(em, StudyApplicationForm.class)
                .leftJoinFetch("applicant", "u")
                .where("announcement.id").eq(announcementId)
                .orderByAsc("submittedAt")
                .orderByAsc("id");

        if (status != null) {
            baseQuery.where("status").eq(status);
        }

        if (lastSubmittedAt != null && lastId != null) {
            baseQuery.whereExpression("submittedAt > :lastSubmittedAt OR (submittedAt = :lastSubmittedAt AND id > :lastId)");

            baseQuery.setParameter("lastSubmittedAt", lastSubmittedAt);
            baseQuery.setParameter("lastId", lastId);
        }

        List<StudyApplicationForm> results = baseQuery
                .setMaxResults(pageSize + 1)
                .getResultList();

        return ApplicationFormPageResult.of(results, pageSize);
    }

    public record ApplicationFormPageResult(
            List<StudyApplicationForm> studyApplicationForms,
            boolean hasNext,
            Long lastId,
            LocalDateTime lastSubmittedAt
    ) {

        public static ApplicationFormPageResult of(List<StudyApplicationForm> results, int pageSize) {
            boolean hasNext = results.size() > pageSize;
            List<StudyApplicationForm> content = hasNext
                    ? results.subList(0, pageSize)
                    : results;

            if (!content.isEmpty()) {
                StudyApplicationForm lastItem = content.get(content.size() - 1);
                return new ApplicationFormPageResult(
                        content,
                        hasNext,
                        lastItem.getId(),
                        lastItem.getSubmittedAt()
                );
            }

            return new ApplicationFormPageResult(content, false, null, null);
        }

    }
}
