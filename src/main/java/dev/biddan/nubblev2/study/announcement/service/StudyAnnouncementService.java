package dev.biddan.nubblev2.study.announcement.service;

import dev.biddan.nubblev2.exception.http.ForbiddenException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementCommand;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyAnnouncementService {

    private final StudyAnnouncementCreator studyAnnouncementCreator;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyAnnouncementDuplicateValidator duplicateValidator;
    private final StudyAnnouncementRepository studyAnnouncementRepository;

    @Transactional
    public StudyAnnouncementInfo.Basic create(
            Long studyGroupId,
            Long currentUserId,
            StudyAnnouncementCommand.Create createCommand) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 스터디 그룹입니다"));

        if (studyGroup.isNotCreator(currentUserId)) {
            throw new ForbiddenException("스터디 공고를 생성할 권한이 없습니다");
        }

        duplicateValidator.validateNoActiveAnnouncement(studyGroupId);

        StudyAnnouncement announcement = studyAnnouncementCreator.create(studyGroup, createCommand);

        return StudyAnnouncementInfo.Basic.from(announcement);
    }

    public StudyAnnouncementInfo.Basic findById(Long announcementId) {
        StudyAnnouncement announcement = studyAnnouncementRepository.findById(announcementId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 모집 공고입니다"));

        return StudyAnnouncementInfo.Basic.from(announcement);
    }
}
