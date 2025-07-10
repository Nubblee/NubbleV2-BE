package dev.biddan.nubblev2.study.problem.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.member.domain.StudyGroupMember;
import dev.biddan.nubblev2.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

@Entity
@Table(name = "problems")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "study_group_id", nullable = false, updatable = false)
    private StudyGroup studyGroup;

    @ElementCollection
    @Column(name = "tag")
    private List<String> tags;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Problem(String title, String url, LocalDate date, User createdBy, StudyGroup studyGroup) {
        Assert.hasText(title, "제목은 필수입니다");
        Assert.hasText(url, "URL은 필수입니다");
        Assert.notNull(date, "날짜는 필수입니다");
        Assert.notNull(createdBy, "생성자는 필수입니다");
        Assert.notNull(studyGroup, "스터디 그룹은 필수입니다");

        this.title = title;
        this.url = url;
        this.date = date;
        this.createdBy = createdBy;
        this.studyGroup = studyGroup;
    }
}
