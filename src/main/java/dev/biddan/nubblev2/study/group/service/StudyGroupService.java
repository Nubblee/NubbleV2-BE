package dev.biddan.nubblev2.study.group.service;

import com.blazebit.persistence.PagedList;
import dev.biddan.nubblev2.exception.http.ForbiddenException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.DifficultyLevel;
import dev.biddan.nubblev2.study.group.repository.StudyGroupBlazeRepository;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import dev.biddan.nubblev2.study.group.repository.StudyGroupView;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupCommand.Create;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo.Detail;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo.PageList;
import dev.biddan.nubblev2.study.member.repository.StudyGroupMemberRepository;
import dev.biddan.nubblev2.study.member.service.StudyGroupAuthorization;
import dev.biddan.nubblev2.study.member.service.StudyGroupAuthorization.StudyGroupPermission;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupCreator studyGroupCreator;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupUpdater studyGroupUpdater;
    private final UserRepository userRepository;
    private final StudyGroupAuthorization studyGroupAuthorization;
    private final StudyGroupBlazeRepository studyGroupBlazeRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;

    public StudyGroupInfo.Detail create(Long creatorId, Create createCommand) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다"));

        StudyGroup studyGroup = studyGroupCreator.create(creator, createCommand);

        return Detail.from(studyGroup);
    }

    @Transactional
    public StudyGroupInfo.Detail update(Long studyGroupId, Long userId, Create updateCommand) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 스터디 그룹입니다"));

        if (studyGroupAuthorization.lacksPermission(studyGroupId, userId, StudyGroupPermission.UPDATE_STUDY_GROUP)) {
            throw new ForbiddenException("스터디 그룹을 수정할 권한이 없습니다");
        }

        studyGroupUpdater.update(studyGroup, updateCommand);

        return StudyGroupInfo.Detail.from(studyGroup);
    }

    @Transactional(readOnly = true)
    public StudyGroupInfo.Detail getById(Long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 스터디 그룹입니다"));

        return StudyGroupInfo.Detail.from(studyGroup);
    }

    @Transactional(readOnly = true)
    public StudyGroupInfo.PageList findList(Integer page, Integer size) {
        PagedList<StudyGroupView> studyGroups = studyGroupBlazeRepository.findStudyGroups(page, size);

        List<Long> studyGroupIds = studyGroups.stream()
                .map(StudyGroupView::id)
                .toList();

        Map<Long, List<DifficultyLevel>> difficultyLevelsMap =
                studyGroupRepository.findDifficultyLevelsMapByStudyGroupIds(studyGroupIds);
        Map<Long, List<StudyGroup.MeetingDay>> meetingDaysMap =
                studyGroupRepository.findMeetingDaysMapByStudyGroupIds(studyGroupIds);
        Map<Long, Long> memberCountsMap =
                studyGroupMemberRepository.countMembersByStudyGroupIds(studyGroupIds);

        return PageList.of(studyGroups, difficultyLevelsMap, meetingDaysMap, memberCountsMap);
    }
}
