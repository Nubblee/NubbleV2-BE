package dev.biddan.nubblev2.study.announcement.service;

import dev.biddan.nubblev2.exception.http.ConflictException;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StudyAnnouncementDuplicateValidator {

    private final StudyAnnouncementRepository studyAnnouncementRepository;

    @Transactional(readOnly = true)
    public void validateNoActiveAnnouncement(Long studyGroupId) {
        if (studyAnnouncementRepository.existsByStudyGroupIdAndStatus(
                studyGroupId, AnnouncementStatus.RECRUITING)) {
            throw new ConflictException("이미 모집중인 공고가 존재합니다");
        }
    }
}

