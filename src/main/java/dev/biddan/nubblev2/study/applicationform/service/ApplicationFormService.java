package dev.biddan.nubblev2.study.applicationform.service;

import dev.biddan.nubblev2.exception.http.ConflictException;
import dev.biddan.nubblev2.exception.http.ForbiddenException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm.ApplicationFormStatus;
import dev.biddan.nubblev2.study.applicationform.repository.ApplicationFormBlazeRepository;
import dev.biddan.nubblev2.study.applicationform.repository.ApplicationFormBlazeRepository.ApplicationFormPageResult;
import dev.biddan.nubblev2.study.applicationform.repository.StudyApplicationFormRepository;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormCommand.Submit;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo;
import dev.biddan.nubblev2.study.member.service.StudyGroupAuthorization;
import dev.biddan.nubblev2.study.member.service.StudyGroupAuthorization.StudyGroupPermission;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationFormService {

    private final StudyAnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final StudyApplicationFormRepository applicationFormRepository;
    private final StudyGroupAuthorization studyGroupAuthorization;
    private final ApplicationFormBlazeRepository applicationFormBlazeRepository;

    @Transactional
    public ApplicationFormInfo.Basic submit(Long announcementId, Long applicantId,
            Submit command) {
        StudyAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 모집 공고입니다"));

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다"));

        if (announcement.isClosed()) {
            throw new ConflictException("마감된 공고에는 지원할 수 없습니다");
        }

        if (applicationFormRepository.existsByAnnouncementAndApplicant(announcement, applicant)) {
            throw new ConflictException("이미 지원한 공고입니다");
        }

        StudyApplicationForm newForm = StudyApplicationForm.builder()
                .applicant(applicant)
                .announcement(announcement)
                .content(command.content())
                .build();

        StudyApplicationForm savedForm = applicationFormRepository.save(newForm);

        return ApplicationFormInfo.Basic.of(savedForm);
    }

    @Transactional(readOnly = true)
    public ApplicationFormInfo.PageList findFormsByAnnouncementId(Long announcementId, Long currentUserId, Long lastId,
            LocalDateTime lastSubmittedAt, String status, int pageSize) {
        StudyAnnouncement announcement = announcementRepository.findByIdWithStudyGroup(announcementId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 모집 공고입니다"));

        Long studyGroupId = announcement.getStudyGroup().getId();
        if (studyGroupAuthorization.lacksPermission(studyGroupId, currentUserId,
                StudyGroupPermission.VIEW_APPLICATION_FORMS)) {
            throw new ForbiddenException("지원서 목록을 조회할 권한이 없습니다");
        }

        ApplicationFormStatus formStatus = status != null ? ApplicationFormStatus.valueOf(status) : null;

        ApplicationFormPageResult result = applicationFormBlazeRepository.findApplicationForms(
                announcementId, lastId, lastSubmittedAt, formStatus, pageSize);

        return ApplicationFormInfo.PageList.of(result);
    }
}
