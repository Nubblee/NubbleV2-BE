package dev.biddan.nubblev2.study.group.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyGroupCapacity {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 50;

    @Column(name = "capacity", nullable = false)
    private Integer value;

    public StudyGroupCapacity(Integer value) {
        validate(value);
        this.value = value;
    }

    private void validate(Integer value) {
        Assert.notNull(value, "정원은 필수입니다");
        Assert.isTrue(value >= MIN_VALUE && value <= MAX_VALUE,
                String.format("정원은 %d명 이상 %d명 이하여야 합니다", MIN_VALUE, MAX_VALUE));
    }
}
