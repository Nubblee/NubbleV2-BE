package dev.biddan.nubblev2.study.member.service;

import dev.biddan.nubblev2.study.member.domain.StudyGroupMember.MemberRole;
import dev.biddan.nubblev2.study.member.repository.StudyGroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StudyGroupAuthorization {

    private final StudyGroupMemberRepository studyGroupMemberRepository;

    @Transactional(readOnly = true)
    public boolean lacksPermission(Long studyGroupId, Long userId, StudyGroupPermission permission) {
        return !switch (permission) {
            case UPDATE_STUDY_GROUP, CREATE_ANNOUNCEMENT, CLOSE_ANNOUNCEMENT, VIEW_APPLICATION_FORMS ->
                    studyGroupMemberRepository.existsMember(
                            studyGroupId, userId, MemberRole.LEADER);
        };
    }

    public enum StudyGroupPermission {
        UPDATE_STUDY_GROUP,
        CLOSE_ANNOUNCEMENT, VIEW_APPLICATION_FORMS, CREATE_ANNOUNCEMENT
    }

}
