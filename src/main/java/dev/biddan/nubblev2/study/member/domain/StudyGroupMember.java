package dev.biddan.nubblev2.study.member.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

@Entity
@Table(name = "study_group_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_study_group_member",
                        columnNames = {"study_group_id", "user_id"}
                )
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class StudyGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_group_id", nullable = false, updatable = false)
    private StudyGroup studyGroup;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role;

    @CreatedDate
    @Column(name = "joined_at",  nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Builder
    public StudyGroupMember(StudyGroup studyGroup, User user, MemberRole role) {
        Assert.notNull(studyGroup, "스터디 그룹은 필수입니다");
        Assert.notNull(user, "사용자는 필수입니다");
        Assert.notNull(role, "멤버 역할은 필수입니다");

        this.studyGroup = studyGroup;
        this.user = user;
        this.role = role;
    }

    public enum MemberRole {
        LEADER,
        MEMBER
    }
}
