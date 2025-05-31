package dev.biddan.nubblev2.study.applicationform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ApplicationFormRejectionReason {

    private static final int MAX_LENGTH = 500;

    @Column(name = "rejection_reason", length = MAX_LENGTH)
    private String value;

    public ApplicationFormRejectionReason(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value != null) {
            Assert.isTrue(value.length() <= MAX_LENGTH,
                    String.format("거부 사유는 %d자를 초과할 수 없습니다", MAX_LENGTH));
        }
    }
}
