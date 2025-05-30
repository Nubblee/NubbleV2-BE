package dev.biddan.nubblev2.study.announcement.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.exception.http.ConflictException;
import dev.biddan.nubblev2.exception.http.ForbiddenException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.study.announcement.controller.StudyAnnouncementApiRequest.StudyAnnouncementCommand;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/study-announcements")
@RequiredArgsConstructor
public class StudyAnnouncementApiController {

    private final StudyAnnouncementService studyAnnouncementService;

    @AuthRequired
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudyAnnouncementApiResponse.Basic> create(
            @CurrentUserId Long currentUserId,
            @RequestBody StudyAnnouncementApiRequest.Create request
    ) {

        StudyAnnouncementInfo.Basic info = studyAnnouncementService.create(request.studyGroupId(), currentUserId,
                request.toCreateCommand());

        StudyAnnouncementApiResponse.Basic response = new StudyAnnouncementApiResponse.Basic(info);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Service
    @RequiredArgsConstructor
    public static class StudyAnnouncementService {

        private final StudyAnnouncementCreator studyAnnouncementCreator;
        private final StudyGroupRepository studyGroupRepository;
        private final StudyAnnouncementDuplicateValidator duplicateValidator;

        @Transactional
        public StudyAnnouncementInfo.Basic create(Long studyGroupId, Long currentUserId,
                StudyAnnouncementCommand.Create createCommand) {
            StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 스터디 그룹입니다"));

            if (!studyGroup.isCreator(currentUserId)) {
                throw new ForbiddenException("스터디 공고를 생성할 권한이 없습니다");
            }

            duplicateValidator.validateNoActiveAnnouncement(studyGroupId);

            StudyAnnouncement announcement = studyAnnouncementCreator.create(studyGroup, createCommand);

            return StudyAnnouncementInfo.Basic.from(announcement);
        }
    }

    @Component
    @RequiredArgsConstructor
    public static class StudyAnnouncementDuplicateValidator {

        private final StudyAnnouncementRepository studyAnnouncementRepository;

        @Transactional(readOnly = true)
        public void validateNoActiveAnnouncement(Long studyGroupId) {
            if (studyAnnouncementRepository.existsByStudyGroupIdAndStatus(
                    studyGroupId, AnnouncementStatus.RECRUITING)) {
                throw new ConflictException("이미 모집중인 공고가 존재합니다");
            }
        }
    }

    public static class StudyAnnouncementApiResponse {

        public record Basic(
                StudyAnnouncementInfo.Basic studyAnnouncement
        ) {

        }
    }

    public static class StudyAnnouncementInfo {

        @Builder
        public record Basic(
                Long id,
                Long studyGroupId,
                String title,
                String description,
                Integer recruitCapacity,
                LocalDateTime startDateTime,
                LocalDateTime endDateTime,
                String status,
                String closedReason,
                LocalDateTime createdAt,
                LocalDateTime closedAt
        ) {

            public static Basic from(StudyAnnouncement announcement) {
                return Basic.builder()
                        .id(announcement.getId())
                        .studyGroupId(announcement.getStudyGroup().getId())
                        .title(announcement.getTitle().getValue())
                        .description(announcement.getDescription().getValue())
                        .recruitCapacity(announcement.getRecruitCapacity().getValue())
                        .startDateTime(announcement.getPeriod().getStartDateTime())
                        .endDateTime(announcement.getPeriod().getEndDateTime())
                        .status(announcement.getStatus().toString())
                        .closedReason(getClosedReason(announcement))
                        .createdAt(announcement.getCreatedAt())
                        .closedAt(announcement.getClosedAt())
                        .build();
            }
        }

        private static String getClosedReason(StudyAnnouncement announcement) {
            return announcement.getClosedReason() != null ? announcement.getClosedReason().toString() : null;
        }
    }

    @Component
    @RequiredArgsConstructor
    public static class StudyAnnouncementCreator {

        private final StudyAnnouncementRepository studyAnnouncementRepository;

        @Transactional
        public StudyAnnouncement create(StudyGroup studyGroup, StudyAnnouncementCommand.Create createCommand) {
            StudyAnnouncement newAnnouncement = StudyAnnouncement.builder()
                    .studyGroup(studyGroup)
                    .title(createCommand.title())
                    .description(createCommand.description())
                    .recruitCapacity(createCommand.recruitCapacity())
                    .startDateTime(createCommand.startDateTime())
                    .endDateTime(createCommand.endDateTime())
                    .build();

            return studyAnnouncementRepository.save(newAnnouncement);
        }
    }
}
