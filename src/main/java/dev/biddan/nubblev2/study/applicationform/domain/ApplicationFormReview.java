package dev.biddan.nubblev2.study.applicationform.domain;

import dev.biddan.nubblev2.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApplicationFormReview {

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    public ApplicationFormReview(User reviewer) {
        Assert.notNull(reviewer, "검토자는 필수입니다");
        this.reviewedBy = reviewer;
        this.reviewedAt = LocalDateTime.now();
    }
}
