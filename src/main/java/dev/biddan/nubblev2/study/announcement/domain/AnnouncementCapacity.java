package dev.biddan.nubblev2.study.announcement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AnnouncementCapacity {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 100;

    @Column(name = "recruit_capacity", nullable = false)
    private Integer value;

    public AnnouncementCapacity(Integer value) {
        validate(value);
        this.value = value;
    }

    private void validate(Integer value) {
        Assert.notNull(value, "모집 인원은 필수입니다");
        Assert.isTrue(value >= MIN_VALUE && value <= MAX_VALUE,
                String.format("모집 인원은 %d명 이상 %d명 이하여야 합니다", MIN_VALUE, MAX_VALUE));
    }
}
