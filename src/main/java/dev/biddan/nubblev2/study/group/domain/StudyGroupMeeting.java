package dev.biddan.nubblev2.study.group.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingDay;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyGroupMeeting {

    private static final int MAX_MEETING_REGION_LENGTH = 100;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    private MeetingType meetingType;

    @Column(name = "meeting_region", length = MAX_MEETING_REGION_LENGTH)
    private String meetingRegion;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "study_group_meeting_days", joinColumns = @JoinColumn(name = "study_group_id"))
    @Column(name = "meeting_day")
    private List<MeetingDay> mainMeetingDays;

    public StudyGroupMeeting(MeetingType meetingType, String meetingRegion, List<MeetingDay> mainMeetingDays) {
        validate(meetingType, meetingRegion, mainMeetingDays);
        this.meetingType = meetingType;
        this.meetingRegion = meetingRegion;
        this.mainMeetingDays = mainMeetingDays;
    }

    private void validate(MeetingType meetingType, String meetingRegion, List<MeetingDay> mainMeetingDays) {
        Assert.notNull(meetingType, "모임 형태는 필수입니다");
        Assert.notEmpty(mainMeetingDays, "주 활동일은 최소 1개 이상 선택해야 합니다");

        if (meetingType == MeetingType.OFFLINE || meetingType == MeetingType.HYBRID) {
            Assert.hasText(meetingRegion, "오프라인 또는 하이브리드 모임의 경우 모임 지역은 필수입니다");
            Assert.isTrue(meetingRegion.length() <= MAX_MEETING_REGION_LENGTH,
                    String.format("모임 지역은 %d자를 초과할 수 없습니다", MAX_MEETING_REGION_LENGTH));
        }
    }
}

