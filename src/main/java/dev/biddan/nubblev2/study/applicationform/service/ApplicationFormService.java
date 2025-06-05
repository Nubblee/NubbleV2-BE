package dev.biddan.nubblev2.study.applicationform.service;

import dev.biddan.nubblev2.exception.http.ConflictException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.study.applicationform.repository.StudyApplicationFormRepository;
import dev.biddan.nubblev2.study.applicationform.service.ApplicationFormService.ApplicationFormCommand.Submit;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import java.time.LocalDateTime;
import lombok.Builder;
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

        if (applicationFormRepository.existsByAnnouncementAndApplicant(announcement, applicant)) {
            throw new ConflictException("이미 지원한 공고입니다");
        }

        StudyApplicationForm newForm = StudyApplicationForm.builder()
                .applicant(applicant)
                .announcement(announcement)
                .content(command.content)
                .build();

        StudyApplicationForm savedForm = applicationFormRepository.save(newForm);

        return ApplicationFormInfo.Basic.from(savedForm);
    }


    public class ApplicationFormCommand {

        public record Submit(
                String content
        ) {

        }
    }

    public class ApplicationFormInfo {

        @Builder
        public record Basic(
                Long id,
                String content,
                String status,
                LocalDateTime submittedAt,
                Long announcementId,
                UserInfo.Public applicant
        ) {

            public static Basic from(StudyApplicationForm savedForm) {
                return Basic.builder()
                        .id(savedForm.getId())
                        .content(savedForm.getContent().getValue())
                        .status(savedForm.getStatus().toString())
                        .submittedAt(savedForm.getSubmittedAt())
                        .announcementId(savedForm.getAnnouncement().getId())
                        .applicant(UserInfo.Public.from(savedForm.getApplicant()))
                        .build();
            }
        }
    }
}
