package dev.biddan.nubblev2.study.group.service;

import dev.biddan.nubblev2.exception.http.ForbiddenException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupCommand.Create;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupCommand.Update;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupCreator studyGroupCreator;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupUpdater studyGroupUpdater;

    public StudyGroupInfo.Private create(Create createCommand) {
        StudyGroup studyGroup = studyGroupCreator.create(createCommand);

        return StudyGroupInfo.Private.from(studyGroup);
    }

    @Transactional
    public StudyGroupInfo.Private update(Long studyGroupId, Long creatorId, Update updateCommand) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 스터디 그룹입니다"));

        if (!studyGroup.isCreator(creatorId)) {
            throw new ForbiddenException("스터디 그룹을 수정할 권한이 없습니다");
        }

        studyGroupUpdater.update(studyGroup, updateCommand);

        return StudyGroupInfo.Private.from(studyGroup);
    }
}
