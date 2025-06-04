package dev.biddan.nubblev2.study.announcement;

import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudyAnnouncementRequestFixture {

    public static StudyAnnouncementApiRequest.Create generateValidCreateRequest(Long studyGroupId) {

        return StudyAnnouncementApiRequest.Create.builder()
                .studyGroupId(studyGroupId)
                .title("Java 백엔드 개발자 스터디 모집")
                .description("Spring Boot와 JPA를 활용한 백엔드 개발 스터디입니다. " +
                        "실무 프로젝트를 통해 함께 성장해요!")
                .recruitCapacity(5)
                .endDate(LocalDate.now().plusDays(1))
                .applicationFormContent(
                        "알고리즘 학습 경험:\n지원 동기:\n사용 가능한 프로그래밍 언어:\n코딩테스트 풀이 경험:\n참여 가능 시간:\n주당 투자 가능 시간:\n")
                .build();
    }
}
