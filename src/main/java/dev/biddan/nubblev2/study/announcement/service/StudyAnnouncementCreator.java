package dev.biddan.nubblev2.study.announcement.service;

import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementRepository;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementCommand;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StudyAnnouncementCreator {

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
                .applicationFormContent(createCommand.applicationFormContent())
                .build();

        return studyAnnouncementRepository.save(newAnnouncement);
    }
}
