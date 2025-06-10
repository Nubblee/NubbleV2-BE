package dev.biddan.nubblev2.study.applicationform.service;

import dev.biddan.nubblev2.exception.http.ConflictException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.study.applicationform.repository.StudyApplicationFormRepository;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormCommand.Submit;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationFormService {

    private final StudyAnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final StudyApplicationFormRepository applicationFormRepository;

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

        return ApplicationFormInfo.Basic.from(savedForm);
    }
}
