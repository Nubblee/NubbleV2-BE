package dev.biddan.nubblev2.study.member.repository;

import dev.biddan.nubblev2.study.member.domain.StudyGroupMember;
import dev.biddan.nubblev2.study.member.domain.StudyGroupMember.MemberRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    @Query("""
            select count(m) > 0
            from StudyGroupMember m
            where m.studyGroup.id = :studyGroupId
              and m.user.id = :userId
              and m.role = :role
            """)
    boolean existsMember(Long studyGroupId, Long userId, MemberRole role);

    @Query("""
            select count(m)
            from StudyGroupMember m
            where m.studyGroup.id = :studyGroupId
            """)
    long countByStudyGroupId(Long studyGroupId);

    @Query("""
            select m
            from StudyGroupMember m
            join fetch m.studyGroup
            where m.user.id = :userId
            order by m.joinedAt DESC
            """)
    List<StudyGroupMember> findByUserIdWithStudyGroupOrderByJoinedAtDesc(Long userId);
}
