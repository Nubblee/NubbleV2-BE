package dev.biddan.nubblev2.study.applicationform.repository;

import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.user.domain.User;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyApplicationFormRepository extends JpaRepository<StudyApplicationForm, Long> {

    boolean existsByAnnouncementAndApplicant(StudyAnnouncement announcement, User applicant);

    @Query("""
            SELECT COUNT(form) 
            FROM StudyApplicationForm form
            WHERE form.announcement.id = :announcementId
            AND form.status = 'APPROVED'
            """)
    Long countApprovedApplicationsByAnnouncementId(Long announcementId);

    @Query("""
            SELECT form.announcement.id, COUNT(form)
            FROM StudyApplicationForm form
            WHERE form.announcement.id IN :announcementIds
            AND form.status = 'APPROVED'
            GROUP BY form.announcement.id
            """)
   List<Long[]> countApprovedApplicationsByAnnouncementIdsRow(List<Long> announcementIds);

    default Map<Long, Long> countApprovedApplicationsByAnnouncementIds(List<Long> announcementIds) {
        if (announcementIds.isEmpty()) {
            return Map.of();
        }

        return countApprovedApplicationsByAnnouncementIdsRow(announcementIds)
                .stream()
                .collect(Collectors.toMap(
                        result -> result[0],
                        result -> result[1]
                ));
    }
}
