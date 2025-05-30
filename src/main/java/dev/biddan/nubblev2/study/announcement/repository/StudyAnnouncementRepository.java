package dev.biddan.nubblev2.study.announcement.repository;

import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyAnnouncementRepository extends JpaRepository<StudyAnnouncement, Long> {

}
