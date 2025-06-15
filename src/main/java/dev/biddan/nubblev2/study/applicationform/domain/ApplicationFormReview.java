package dev.biddan.nubblev2.study.applicationform.domain;

import dev.biddan.nubblev2.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ApplicationFormReview {

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    public ApplicationFormReview(User reviewerId, LocalDateTime reviewedAt) {
        Assert.notNull(reviewerId, "검토자는 필수입니다");
        Assert.notNull(reviewedAt, "검토 시간은 필수입니다");

        this.reviewer = reviewerId;
        this.reviewedAt = reviewedAt;
    }
}
