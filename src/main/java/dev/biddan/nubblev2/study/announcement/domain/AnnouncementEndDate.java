package dev.biddan.nubblev2.study.announcement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AnnouncementEndDate {

    @Column(name = "end_date", nullable = false)
    private LocalDate value;

    public AnnouncementEndDate(LocalDate value) {
        validate(value);

        this.value = value;
    }

    private static void validate(LocalDate endDate) {
        Assert.notNull(endDate, "모집 마감 일시는 필수입니다");
        Assert.isTrue(endDate.isAfter(LocalDate.now()), "모집 마감 일시은 오늘 이후여야 합니다");
    }
}
