package dev.biddan.nubblev2.study.announcement.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Table(name = "study_announcements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @Embedded
    private StudyAnnouncementTitle title;

    @Embedded
    private StudyAnnouncementDescription description;

    @Embedded
    private StudyAnnouncementCapacity recruitCapacity;

    @Embedded
    private StudyAnnouncementPeriod period;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnnouncementStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "closed_reason")
    private ClosedReason closedReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Builder
    public StudyAnnouncement(StudyGroup studyGroup, String title, String description,
            Integer recruitCapacity, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Assert.notNull(studyGroup, "스터디 그룹은 필수입니다");
        this.studyGroup = studyGroup;

        this.title = new StudyAnnouncementTitle(title);
        this.description = new StudyAnnouncementDescription(description);
        this.recruitCapacity = new StudyAnnouncementCapacity(recruitCapacity);
        this.period = new StudyAnnouncementPeriod(startDateTime, endDateTime, studyGroup);
        this.status = AnnouncementStatus.RECRUITING;
        this.createdAt = LocalDateTime.now();
    }

    public enum AnnouncementStatus {
        RECRUITING,
        CLOSED
    }

    public enum ClosedReason {
        MANUAL,
        AUTO_CAPACITY_REACHED,
        AUTO_DEADLINE_REACHED
    }
}
