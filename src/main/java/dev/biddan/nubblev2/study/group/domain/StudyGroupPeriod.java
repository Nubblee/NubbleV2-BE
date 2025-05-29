package dev.biddan.nubblev2.study.group.domain;

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
public class StudyGroupPeriod {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public StudyGroupPeriod(LocalDate startDate, LocalDate endDate) {
        validate(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private void validate(LocalDate startDate, LocalDate endDate) {
        Assert.notNull(startDate, "시작일은 필수입니다");
        Assert.notNull(endDate, "종료일은 필수입니다");
        Assert.isTrue(!endDate.isBefore(startDate), "종료일은 시작일보다 빠를 수 없습니다");
    }
}
