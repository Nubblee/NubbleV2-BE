package dev.biddan.nubblev2.study.problem.service;

import dev.biddan.nubblev2.exception.http.ForbiddenException;
import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import dev.biddan.nubblev2.study.member.domain.StudyGroupMember;
import dev.biddan.nubblev2.study.member.repository.StudyGroupMemberRepository;
import dev.biddan.nubblev2.study.problem.controller.dto.ProblemApiRequest;
import dev.biddan.nubblev2.study.problem.domain.Problem;
import dev.biddan.nubblev2.study.problem.repository.ProblemRepository;
import dev.biddan.nubblev2.study.problem.service.dto.ProblemInfo;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProblemInfo createProblem(Long studyGroupId, ProblemApiRequest.Create request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다"));
        
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new NotFoundException("스터디 그룹을 찾을 수 없습니다"));
        
        validateStudyGroupLeader(user, studyGroup);

        Problem problem = Problem.builder()
                .title(request.title())
                .url(request.url())
                .date(request.date())
                .createdBy(user)
                .studyGroup(studyGroup)
                .build();

        Problem savedProblem = problemRepository.save(problem);
        return ProblemInfo.from(savedProblem);
    }

    @Transactional
    public void deleteProblem(Long problemId, Long userId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new NotFoundException("문제를 찾을 수 없습니다"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다"));

        validateStudyGroupLeader(user, problem.getStudyGroup());

        if (problem.isDeleted()) {
            return;
        }

        problem.softDelete();
        problemRepository.save(problem);
    }

    @Transactional(readOnly = true)
    public List<ProblemInfo> findProblemsWithOffset(Long studyGroupId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Problem> problems = problemRepository.findByStudyGroupIdOrderByCreatedAtDesc(studyGroupId, pageable);
        
        return problems.stream()
                .map(ProblemInfo::from)
                .toList();
    }

    private void validateStudyGroupLeader(User user, StudyGroup studyGroup) {
        StudyGroupMember member = studyGroupMemberRepository
                .findByStudyGroupAndUser(studyGroup, user)
                .orElseThrow(() -> new ForbiddenException("스터디 그룹의 멤버가 아닙니다"));

        if (member.getRole() != StudyGroupMember.MemberRole.LEADER) {
            throw new ForbiddenException("문제 등록은 스터디 그룹장만 가능합니다");
        }
    }
}
