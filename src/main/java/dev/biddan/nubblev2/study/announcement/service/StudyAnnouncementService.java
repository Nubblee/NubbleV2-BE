package dev.biddan.nubblev2.study.announcement.service;

import static dev.biddan.nubblev2.study.member.service.StudyGroupAuthorization.StudyGroupPermission.CLOSE_ANNOUNCEMENT;
import static dev.biddan.nubblev2.study.member.service.StudyGroupAuthorization.StudyGroupPermission.CREATE_ANNOUNCEMENT;

import dev.biddan.nubblev2.exception.http.ConflictException;
import dev.biddan.nubblev2.exception.http.ForbiddenException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.exception.http.UnprocessableEntityException;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementCommand;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo.Basic;
import dev.biddan.nubblev2.study.applicationform.repository.StudyApplicationFormRepository;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import dev.biddan.nubblev2.study.member.repository.StudyGroupMemberRepository;
import dev.biddan.nubblev2.study.member.service.StudyGroupAuthorization;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyAnnouncementService {

    private final StudyAnnouncementCreator studyAnnouncementCreator;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyAnnouncementRepository studyAnnouncementRepository;
    private final Clock clock;
    private final StudyGroupAuthorization studyGroupAuthorization;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final StudyApplicationFormRepository studyApplicationFormRepository;

    @Transactional
    public StudyAnnouncementInfo.Basic create(
            Long studyGroupId,
            Long currentUserId,
            StudyAnnouncementCommand.Create createCommand) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 스터디 그룹입니다"));

        if (studyGroupAuthorization.lacksPermission(studyGroupId, currentUserId, CREATE_ANNOUNCEMENT)) {
            throw new ForbiddenException("스터디 공고를 생성할 권한이 없습니다");
        }

        if (studyAnnouncementRepository.existsByStudyGroupIdAndStatus(
                studyGroupId, AnnouncementStatus.RECRUITING)) {
            throw new ConflictException("이미 모집중인 공고가 존재합니다");
        }

        validateCapacityLimit(studyGroup, createCommand.recruitCapacity());

        StudyAnnouncement announcement = studyAnnouncementCreator.create(studyGroup, createCommand);

        return StudyAnnouncementInfo.Basic.from(announcement);
    }

    public StudyAnnouncementInfo.WithMeta findById(Long announcementId) {
        StudyAnnouncement announcement = studyAnnouncementRepository.findById(announcementId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 모집 공고입니다"));

        int approvedCount = studyApplicationFormRepository.countApprovedApplicationsByAnnouncementId(announcementId)
                .intValue();

        return StudyAnnouncementInfo.WithMeta.of(announcement, approvedCount);
    }

    private void validateCapacityLimit(StudyGroup studyGroup, Integer recruitCapacity) {
        Integer studyGroupCapacity = studyGroup.getCapacity().getValue();
        long currentMemberCount = studyGroupMemberRepository.countByStudyGroupId(studyGroup.getId());
        long totalMembersAfterRecruit = currentMemberCount + recruitCapacity;

        if (totalMembersAfterRecruit > studyGroupCapacity) {
            throw new UnprocessableEntityException(
                    String.format("모집 인원이 남은 정원을 초과합니다. 모집 최대 %d명까지 모집할 수 있습니다",
                            studyGroupCapacity - currentMemberCount)
            );
        }
    }

    @Transactional
    public Basic close(Long announcementId, Long currentUserId) {
        StudyAnnouncement announcement = studyAnnouncementRepository.findByIdWithStudyGroup(announcementId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 모집 공고입니다"));

        if (studyGroupAuthorization.lacksPermission(
                announcement.getStudyGroup().getId(), currentUserId, CLOSE_ANNOUNCEMENT)) {
            throw new ForbiddenException("스터디 공고를 마감할 권한이 없습니다");
        }

        announcement.close(StudyAnnouncement.ClosedReason.MANUAL, LocalDateTime.now(clock));

        return StudyAnnouncementInfo.Basic.from(announcement);
    }
}
