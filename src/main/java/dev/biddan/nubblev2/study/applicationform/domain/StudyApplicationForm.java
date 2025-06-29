package dev.biddan.nubblev2.study.applicationform.domain;

import dev.biddan.nubblev2.exception.http.ConflictException;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "study_application_forms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_application_form_announcement_applicant",
                        columnNames = {"announcement_id", "applicant_id"}
                )
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class StudyApplicationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private StudyAnnouncement announcement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Embedded
    private ApplicationFormContent content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationFormStatus status;

    @Embedded
    private ApplicationFormRejectionReason rejectionReason;

    @Column(name = "submitted_at", nullable = false)
    @CreatedDate
    private LocalDateTime submittedAt;

    @Embedded
    private ApplicationFormReview review;


    @Builder
    public StudyApplicationForm(StudyAnnouncement announcement, User applicant, String content) {
        Assert.notNull(announcement, "스터디 공고는 필수입니다");
        this.announcement = announcement;

        Assert.notNull(applicant, "지원자는 필수입니다");
        this.applicant = applicant;

        this.content = new ApplicationFormContent(content);
        this.status = ApplicationFormStatus.SUBMITTED;
    }

    public void approve(User reviewer, LocalDateTime reviewedAt) {
        if (status != ApplicationFormStatus.SUBMITTED) {
            throw new ConflictException("제출된 상태일 때만 수락이 가능합니다");
        }

        this.status = ApplicationFormStatus.APPROVED;
        this.review = new ApplicationFormReview(reviewer, reviewedAt);
    }

    public enum ApplicationFormStatus {
        SUBMITTED,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}
