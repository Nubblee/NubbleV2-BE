package dev.biddan.nubblev2.study.announcement;

import dev.biddan.nubblev2.study.announcement.controller.StudyAnnouncementApiRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudyAnnouncementRequestFixture {

    public static StudyAnnouncementApiRequest.Create generateValidCreateRequest(Long studyGroupId,
            LocalDate studyGroupEndDate) {
        LocalDateTime now = LocalDateTime.now();
        // 스터디 그룹 종료일보다 하루 이른 마감일로 설정
        LocalDateTime safeEndDateTime = studyGroupEndDate.minusDays(1).atTime(23, 59, 59);

        return StudyAnnouncementApiRequest.Create.builder()
                .studyGroupId(studyGroupId)
                .title("Java 백엔드 개발자 스터디 모집")
                .description("Spring Boot와 JPA를 활용한 백엔드 개발 스터디입니다. " +
                        "실무 프로젝트를 통해 함께 성장해요!")
                .recruitCapacity(5)
                .startDateTime(now)
                .endDateTime(safeEndDateTime)
                .build();
    }
}
