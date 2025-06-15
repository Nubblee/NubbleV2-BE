package dev.biddan.nubblev2.study.applicationform.repository;

import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.user.domain.User;
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
}
