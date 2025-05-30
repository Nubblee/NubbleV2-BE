package dev.biddan.nubblev2.study.announcement.repository;

import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyAnnouncementRepository extends JpaRepository<StudyAnnouncement, Long> {

    boolean existsByStudyGroupIdAndStatus(Long studyGroupId, AnnouncementStatus announcementStatus);
}
