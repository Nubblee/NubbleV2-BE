package dev.biddan.nubblev2.study.group.service;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupCommand.Create;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo.Private;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupCreator studyGroupCreator;

    public Private create(Create createCommand) {
        StudyGroup studyGroup = studyGroupCreator.create(createCommand);

        return StudyGroupInfo.Private.from(studyGroup);
    }
}
