package dev.biddan.nubblev2.study.announcement;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import dev.biddan.nubblev2.study.applicationform.ApplicationFormApiTestClient;
import dev.biddan.nubblev2.study.applicationform.ApplicationFormRequestFixture;
import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;
import dev.biddan.nubblev2.study.group.StudyGroupApiTestClient;
import dev.biddan.nubblev2.study.group.StudyGroupRequestFixture;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 공고 목록 조회 테스트")
class StudyAnnouncementListTest extends AbstractIntegrationTest {

    private String ownerAuthSessionId;

    @BeforeEach
    void setUp() {
        // given: 사용자 생성 및 로그인 (공통 부분만)
        UserApiRequest.Register ownerRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(ownerRegisterRequest);

        AuthApiRequest.Login ownerLoginRequest = new AuthApiRequest.Login(
                ownerRegisterRequest.loginId(),
                ownerRegisterRequest.password()
        );

        Response ownerLoginResponse = AuthApiTestClient.login(ownerLoginRequest);
        Cookie ownerSessionCookie = ownerLoginResponse.getDetailedCookie(
                AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        ownerAuthSessionId = ownerSessionCookie.getValue();
    }

    @Nested
    @DisplayName("최신순 정렬 검증")
    class LatestOrderTest {

        @Test
        @DisplayName("시간 간격을 두고 생성한 공고들이 최신순으로 정렬된다")
        void announcements_should_be_ordered_by_latest_created_time() {
            // given: 기준 시간 설정 (2024년 6월 1일 오전 9시)
            LocalDateTime baseTime = LocalDateTime.of(2024, 6, 1, 9, 0);
            systemClock.setFixedTime(baseTime);

            List<Long> announcementIds = new ArrayList<>();

            // given: 30분 간격으로 5개의 공고 생성
            for (int i = 0; i < 5; i++) {
                Long studyGroupId = createStudyGroup();
                Long announcementId = createAnnouncement(studyGroupId);
                announcementIds.add(announcementId);

                // 다음 공고를 위해 30분 진행
                if (i < 4) {
                    systemClock.advanceTime(Duration.ofMinutes(30));
                }
            }

            // when: 공고 목록 조회
            Response listResponse = StudyAnnouncementApiTestClient.findList(null, null, null);

            // then: 최신순으로 정렬되어 조회됨 (4번 -> 3번 -> 2번 -> 1번 -> 0번)
            listResponse.then()
                    .statusCode(200)
                    .body("announcements", hasSize(5))
                    .body("announcements[0].id", equalTo(announcementIds.get(4).intValue()))
                    .body("announcements[1].id", equalTo(announcementIds.get(3).intValue()))
                    .body("announcements[2].id", equalTo(announcementIds.get(2).intValue()))
                    .body("announcements[3].id", equalTo(announcementIds.get(1).intValue()))
                    .body("announcements[4].id", equalTo(announcementIds.get(0).intValue()));
        }

        @Test
        @DisplayName("동일한 시간에 생성된 공고들은 ID 순으로 정렬된다")
        void announcements_created_at_same_time_should_be_ordered_by_id() {
            // given: 같은 시간에 여러 공고 생성
            LocalDateTime fixedTime = LocalDateTime.of(2024, 6, 1, 12, 0);
            systemClock.setFixedTime(fixedTime);

            List<Long> announcementIds = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Long studyGroupId = createStudyGroup();
                Long announcementId = createAnnouncement(studyGroupId);
                announcementIds.add(announcementId);
            }

            // when: 목록 조회
            Response listResponse = StudyAnnouncementApiTestClient.findList(null, null, null);

            // then: 시간이 같으므로 ID가 빠른 순으로 정렬됨
            listResponse.then()
                    .statusCode(200)
                    .body("announcements", hasSize(3))
                    .body("announcements[2].id", equalTo(announcementIds.get(0).intValue()))
                    .body("announcements[1].id", equalTo(announcementIds.get(1).intValue()))
                    .body("announcements[0].id", equalTo(announcementIds.get(2).intValue()));
        }

    }

    @Nested
    @DisplayName("상태별 필터링 및 모두 조회")
    class StatusFilteringTest {

        @Test
        @DisplayName("[TODO!] RECRUITING 상태의 공고만 필터링하여 조회할 수 있다")
        void filter_recruiting_announcements_only() {
            // given: 시간차를 두고 다양한 상태의 공고 생성
            LocalDateTime baseTime = LocalDateTime.of(2024, 6, 1, 10, 0);
            systemClock.setFixedTime(baseTime);

            // 모집중인 공고 2개 생성
            List<Long> recruitingIds = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Long studyGroupId = createStudyGroup();
                Long announcementId = createAnnouncement(studyGroupId);
                recruitingIds.add(announcementId);
                systemClock.advanceTime(Duration.ofMinutes(30));
            }

            // 1개 공고 마감 처리
            StudyAnnouncementApiTestClient.close(recruitingIds.get(0), ownerAuthSessionId);

            // when: RECRUITING 상태만 필터링 조회
            Response response = StudyAnnouncementApiTestClient.findList(List.of("RECRUITING"), null, null);

            // then: RECRUITING 상태의 공고만 조회됨
            response.then()
                    .statusCode(200)
                    .body("announcements", hasSize(1))
                    .body("announcements[0].status", equalTo("RECRUITING"));
        }

        @Test
        @DisplayName("[TODO!] 여러 상태를 동시에 필터링할 수 있다")
        void filter_multiple_statuses() {
            // given: 다양한 상태의 공고 생성
            LocalDateTime baseTime = LocalDateTime.of(2024, 6, 1, 16, 0);
            systemClock.setFixedTime(baseTime);

            List<Long> recruitingIds = new ArrayList<>();

            // given: 공고 4개 생성
            for (int i = 0; i < 4; i++) {
                Long studyGroupId = createStudyGroup();
                Long announcementId = createAnnouncement(studyGroupId);
                recruitingIds.add(announcementId);
                systemClock.advanceTime(Duration.ofMinutes(15));
            }

            // 2개 공고 마감 처리
            for (int i = 0; i < 2; i++) {
                StudyAnnouncementApiTestClient.close(recruitingIds.get(i), ownerAuthSessionId);
            }

            // when: RECRUITING과 CLOSED 모두 조회
            Response response = StudyAnnouncementApiTestClient.findList(List.of("RECRUITING", "CLOSED"), null, null);

            // then: 모든 공고가 최신순으로 조회됨
            response.then()
                    .statusCode(200)
                    .body("announcements", hasSize(4))
                    .body("announcements[0].id", equalTo(recruitingIds.get(3).intValue()))
                    .body("announcements[3].id", equalTo(recruitingIds.get(0).intValue()));
        }

        @Test
        @DisplayName("[TODO!] 필터 없이 모든 공고를 조회할 수 있다")
        void get_all_announcements_without_filter() {
            // given: 다양한 시점과 상태의 공고 생성
            LocalDateTime startTime = LocalDateTime.of(2024, 6, 1, 9, 0);
            systemClock.setFixedTime(startTime);

            // given: 공고 4개 생성
            List<Long> recruitingIds = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                Long studyGroupId = createStudyGroup();
                Long announcementId = createAnnouncement(studyGroupId);
                recruitingIds.add(announcementId);
                systemClock.advanceTime(Duration.ofMinutes(15));
            }

            // 2개 공고 마감 처리
            for (int i = 0; i < 2; i++) {
                StudyAnnouncementApiTestClient.close(recruitingIds.get(i), ownerAuthSessionId);
            }

            // when: 필터 없이 전체 조회
            Response response = StudyAnnouncementApiTestClient.findList(null, null, null);

            // then: 모든 공고가 최신순으로 조회됨
            response.then()
                    .statusCode(200)
                    .body("announcements", hasSize(4))
                    .body("meta.page", equalTo(1))
                    .body("meta.totalSize", equalTo(4));
        }

    }

    @Nested
    @DisplayName("페이징 테스트")
    class PagingTest {

        @Test
        @DisplayName("기본 페이지 크기(20)로 페이징이 정상 동작한다")
        void default_page_size_paging_works_correctly() {
            // given: 25개 공고 생성 (기본 페이지 크기보다 많이)
            LocalDateTime baseTime = LocalDateTime.of(2024, 6, 1, 8, 0);
            systemClock.setFixedTime(baseTime);

            for (int i = 0; i < 25; i++) {
                Long studyGroupId = createStudyGroup();
                createAnnouncement(studyGroupId);
                systemClock.advanceTime(Duration.ofMinutes(10));
            }

            // when: 첫 번째 페이지 조회 (기본값: page=1, size=20)
            Response firstPageResponse = StudyAnnouncementApiTestClient.findList(null, null, null);

            // then: 첫 번째 페이지 검증
            firstPageResponse.then()
                    .statusCode(200)
                    .body("announcements", hasSize(20))
                    .body("meta.page", equalTo(1))
                    .body("meta.totalPages", equalTo(2))
                    .body("meta.totalSize", equalTo(25))
                    .body("meta.hasNext", equalTo(true))
                    .body("meta.hasPrevious", equalTo(false));

            // when: 두 번째 페이지 조회
            Response secondPageResponse = StudyAnnouncementApiTestClient.findList(null, 2, null);

            // then: 두 번째 페이지 검증
            secondPageResponse.then()
                    .statusCode(200)
                    .body("announcements", hasSize(5))
                    .body("meta.page", equalTo(2))
                    .body("meta.totalPages", equalTo(2))
                    .body("meta.totalSize", equalTo(25))
                    .body("meta.hasNext", equalTo(false))
                    .body("meta.hasPrevious", equalTo(true));
        }

        @Test
        @DisplayName("사용자 정의 페이지 크기로 페이징할 수 있다")
        void custom_page_size_paging_works() {
            // given: 15개 공고 생성
            LocalDateTime baseTime = LocalDateTime.of(2024, 6, 1, 14, 0);
            systemClock.setFixedTime(baseTime);

            for (int i = 0; i < 15; i++) {
                Long studyGroupId = createStudyGroup();
                createAnnouncement(studyGroupId);
                systemClock.advanceTime(Duration.ofMinutes(5));
            }

            // when: 페이지 크기 5로 첫 번째 페이지 조회
            Response response = StudyAnnouncementApiTestClient.findList(List.of(), 1, 5);

            // then: 페이징 정보 검증
            response.then()
                    .statusCode(200)
                    .body("announcements", hasSize(5))
                    .body("meta.page", equalTo(1))
                    .body("meta.totalPages", equalTo(3))
                    .body("meta.totalSize", equalTo(15))
                    .body("meta.hasNext", equalTo(true))
                    .body("meta.hasPrevious", equalTo(false));
        }

        @Test
        @DisplayName("빈 페이지 요청시 적절한 응답을 반환한다")
        void empty_page_request_returns_appropriate_response() {
            // given: 5개 공고만 생성
            LocalDateTime baseTime = LocalDateTime.of(2024, 6, 1, 18, 0);
            systemClock.setFixedTime(baseTime);

            for (int i = 0; i < 5; i++) {
                Long studyGroupId = createStudyGroup();
                createAnnouncement(studyGroupId);
                systemClock.advanceTime(Duration.ofMinutes(10));
            }

            // when: 존재하지 않는 페이지 요청 (page=2, size=10)
            Response response = StudyAnnouncementApiTestClient.findList(null, 2, 10);

            // then: 빈 결과 반환
            response.then()
                    .statusCode(200)
                    .body("announcements", hasSize(0))
                    .body("meta.page", equalTo(2))
                    .body("meta.totalPages", equalTo(1))
                    .body("meta.totalSize", equalTo(5))
                    .body("meta.hasNext", equalTo(false))
                    .body("meta.hasPrevious", equalTo(true));
        }

    }

    @Test
    @DisplayName("지원자 수락 후 목록에서 acceptedCount가 정확히 반영된다")
    void approvedCount_reflects_after_approval() {
        // given: 공고 생성
        Long studyGroup1Id = createStudyGroup();
        Long announcement1Id = createAnnouncement(studyGroup1Id);

        // given: 공고에 지원서 제출
        Long form1Id = createApplicantAndSubmit(announcement1Id);

        // given: 지원서 수락
        ApplicationFormApiTestClient.approve(announcement1Id, form1Id, ownerAuthSessionId);

        // when: 목록 조회
        Response response = StudyAnnouncementApiTestClient.findList(null, null, null);

        // then: 공고에 수락 인원이 정확히 반영됨
        response.then()
                .statusCode(200)
                .body("announcements", hasSize(1))
                .body("announcements[0].id", equalTo(announcement1Id.intValue()))
                .body("announcements[0].meta.approvedCount", equalTo(1));
    }

    @Test
    @DisplayName("공고 목록에 스터디 그룹 정보가 포함되어 조회된다")
    void announcement_list_includes_study_group_information() {
        // given: 특정 스터디 그룹 정보로 공고 생성
        LocalDateTime baseTime = LocalDateTime.of(2024, 6, 1, 10, 0);
        systemClock.setFixedTime(baseTime);

        // given: 특정 스터디 그룹 생성
        StudyGroupApiRequest.Create studyGroupRequest = StudyGroupApiRequest.Create.builder()
                .name("백엔드 마스터 스터디")
                .description("Spring Boot와 JPA를 깊이 있게 다루는 스터디입니다.")
                .capacity(8)
                .languages(List.of("JAVA", "KOTLIN"))
                .mainLanguage("JAVA")
                .difficultyLevels(List.of("LV3", "LV4"))
                .problemPlatforms(List.of("PROGRAMMERS", "BAEKJOON"))
                .meetingType("HYBRID")
                .meetingRegion("서울시 강남구")
                .mainMeetingDays(List.of("WED", "SAT"))
                .build();

        Response studyGroupResponse = StudyGroupApiTestClient.create(studyGroupRequest, ownerAuthSessionId);
        Long studyGroupId = studyGroupResponse.jsonPath().getLong("studyGroup.id");

        // given: 해당 스터디 그룹으로 공고 생성
        StudyAnnouncementApiRequest.Create announcementRequest = StudyAnnouncementApiRequest.Create.builder()
                .studyGroupId(studyGroupId)
                .title("백엔드 개발자 모집")
                .description("실무 경험을 쌓고 싶은 백엔드 개발자를 모집합니다.")
                .recruitCapacity(3)
                .endDate(LocalDate.now().plusDays(7))
                .applicationFormContent("자기소개와 개발 경험을 적어주세요.")
                .build();

        StudyAnnouncementApiTestClient.create(announcementRequest, ownerAuthSessionId);

        // when: 공고 목록 조회
        Response response = StudyAnnouncementApiTestClient.findList(null, null, null);

        // then: 스터디 그룹 정보가 포함되어 조회됨
        response.then()
                .statusCode(200)
                .body("announcements", hasSize(1))
                .body("announcements[0].title", equalTo("백엔드 개발자 모집"))
                .body("announcements[0].studyGroup.id", equalTo(studyGroupId.intValue()))
                .body("announcements[0].studyGroup.name", equalTo("백엔드 마스터 스터디"))
                .body("announcements[0].studyGroup.mainLanguage", equalTo("JAVA"))
                .body("announcements[0].studyGroup.capacity", equalTo(8))
                .body("announcements[0].studyGroup.meetingType", equalTo("HYBRID"))
                .body("announcements[0].studyGroup.meetingRegion", equalTo("서울시 강남구"))
                .body("announcements[0].studyGroup.languages", equalTo(studyGroupRequest.languages()))
                .body("announcements[0].studyGroup.difficultyLevels", equalTo(studyGroupRequest.difficultyLevels()));
    }

    private Long createStudyGroup() {
        StudyGroupApiRequest.Create createRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response createResponse = StudyGroupApiTestClient.create(createRequest, ownerAuthSessionId);
        return createResponse.jsonPath().getLong("studyGroup.id");
    }

    private Long createAnnouncement(Long studyGroupId) {
        StudyAnnouncementApiRequest.Create request = StudyAnnouncementRequestFixture.generateValidCreateRequest(
                studyGroupId);
        Response response = StudyAnnouncementApiTestClient.create(request, ownerAuthSessionId);
        return response.jsonPath().getLong("studyAnnouncement.id");
    }

    private Long createApplicantAndSubmit(Long announcementId) {
        // 지원자 생성 및 로그인
        UserApiRequest.Register applicantRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(applicantRequest);

        AuthApiRequest.Login applicantLogin = new AuthApiRequest.Login(
                applicantRequest.loginId(), applicantRequest.password());
        Response loginResponse = AuthApiTestClient.login(applicantLogin);
        Cookie applicantCookie = loginResponse.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);

        // 지원서 제출
        ApplicationFormApiRequest.Submit submitRequest = ApplicationFormRequestFixture.generateValidSubmitRequest();
        Response submitResponse = ApplicationFormApiTestClient.submit(
                announcementId, submitRequest, applicantCookie.getValue());

        return submitResponse.jsonPath().getLong("applicationForm.id");
    }
}
