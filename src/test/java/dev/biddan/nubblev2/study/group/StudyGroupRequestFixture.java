package dev.biddan.nubblev2.study.group;

import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest.Create;
import java.util.List;

public class StudyGroupRequestFixture {

    public static Create generateValidCreateRequest() {
        return StudyGroupApiRequest.Create.builder()
                .name("알고리즘 마스터 스터디")
                .description("프로그래머스와 백준 알고리즘 문제를 함께 풀며 성장하는 스터디입니다.")
                .capacity(10)
                .languages(List.of("JAVA", "PYTHON"))
                .mainLanguage("JAVA")
                .difficultyLevels(List.of("LV1", "LV2"))
                .problemPlatforms(List.of("PROGRAMMERS", "BAEKJOON"))
                .meetingType("ONLINE")
                .meetingRegion("서울시/강남구")
                .mainMeetingDays(List.of("MON", "WED", "FRI"))
                .build();
    }
}
