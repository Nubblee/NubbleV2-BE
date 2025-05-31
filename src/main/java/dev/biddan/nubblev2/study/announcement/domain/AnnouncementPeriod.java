package dev.biddan.nubblev2.study.announcement.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AnnouncementPeriod {

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    public AnnouncementPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime, StudyGroup studyGroup) {
        validate(startDateTime, endDateTime, studyGroup);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    private void validate(LocalDateTime startDateTime, LocalDateTime endDateTime, StudyGroup studyGroup) {
        Assert.notNull(startDateTime, "모집 시작 일시는 필수입니다");
        Assert.notNull(endDateTime, "모집 마감 일시는 필수입니다");
        Assert.isTrue(!endDateTime.isBefore(startDateTime),
                "모집 마감 일시는 시작 일시보다 빠를 수 없습니다");

        LocalDate studyGroupEndDate = studyGroup.getPeriod().getEndDate();
        LocalDateTime studyGroupEndDateTime = studyGroupEndDate.atTime(23, 59, 59);

        Assert.isTrue(!endDateTime.isAfter(studyGroupEndDateTime),
                "모집 마감 일시는 스터디 그룹 종료일을 초과할 수 없습니다");
    }
}
