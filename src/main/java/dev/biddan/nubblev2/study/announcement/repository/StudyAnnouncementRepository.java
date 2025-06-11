package dev.biddan.nubblev2.study.announcement.repository;

import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyAnnouncementRepository extends JpaRepository<StudyAnnouncement, Long> {

    boolean existsByStudyGroupIdAndStatus(Long studyGroupId, AnnouncementStatus announcementStatus);

    @Query("SELECT a FROM StudyAnnouncement a "
            + "JOIN FETCH a.studyGroup g "
            + "WHERE a.id = :announcementId")
    Optional<StudyAnnouncement> findByIdWithStudyGroup(Long announcementId);
}
