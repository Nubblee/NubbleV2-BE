package dev.biddan.nubblev2.study.applicationform;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.study.announcement.StudyAnnouncementApiTestClient;
import dev.biddan.nubblev2.study.announcement.StudyAnnouncementRequestFixture;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지원서 목록 조회 테스트")
class ApplicationFormListTest extends AbstractIntegrationTest {

    private String ownerAuthSessionId;
    private Long announcementId;
    private List<Long> applicationFormIds;

    @BeforeEach
    void setUp() {
        // given: 스터디 그룹 소유자 생성
        ownerAuthSessionId = createUserAndLogin();

        // given: 스터디 그룹 및 공고 생성
        Long studyGroupId = createStudyGroup(ownerAuthSessionId);
        announcementId = createAnnouncement(studyGroupId, ownerAuthSessionId);

        // given: 다수의 지원자와 지원서 생성
        applicationFormIds = new ArrayList<>();
    }

    @Test
    @DisplayName("페이징이 정상 동작한다(오래된 순으로 조회) - 첫 페이지부터 마지막까지")
    void pagingWorksCorrectly() {
        // given: 8개의 지원서 생성 (2페이지 분량)
        createApplicationFormsWithTimeGap(8);

        // when: 첫 번째 페이지 조회
        Response firstPageResponse = ApplicationFormApiTestClient.findList(
                announcementId, null, null, null, ownerAuthSessionId);

        // then: 첫 페이지 검증 (5개, 다음 페이지 있음)
        firstPageResponse.then()
                .statusCode(200)
                .body("meta.hasNext", equalTo(true))
                .body("meta.lastId", notNullValue())
                .body("meta.lastSubmittedAt", notNullValue())
                .body("applicationForms[0].id", equalTo(applicationFormIds.get(0).intValue()))
                .body("applicationForms[4].id", equalTo(applicationFormIds.get(4).intValue()));

        // when: 두 번째 페이지 조회
        Long lastId = firstPageResponse.jsonPath().getLong("applicationForms[4].id");
        String lastSubmittedAtStr = firstPageResponse.jsonPath().getString("applicationForms[4].submittedAt");
        LocalDateTime lastSubmittedAt = LocalDateTime.parse(lastSubmittedAtStr);

        Response secondPageResponse = ApplicationFormApiTestClient.findList(
                announcementId, lastId, lastSubmittedAt, null, ownerAuthSessionId);

        // then: 두 번째 페이지 검증 (3개, 마지막 페이지)
        secondPageResponse.then()
                .statusCode(200)
                .body("meta.hasNext", equalTo(false))
                .body("meta.lastId", notNullValue())
                .body("meta.lastSubmittedAt", notNullValue())
                .body("applicationForms[0].id", equalTo(applicationFormIds.get(5).intValue()))
                .body("applicationForms[2].id", equalTo(applicationFormIds.get(7).intValue()));

        // then: 전체 데이터 누락/중복 없음 검증
        List<Integer> firstPageIds = firstPageResponse.jsonPath().getList("applicationForms.id", Integer.class);
        List<Integer> secondPageIds = secondPageResponse.jsonPath().getList("applicationForms.id", Integer.class);

        assertThat(firstPageIds).hasSize(5);
        assertThat(secondPageIds).hasSize(3);
        assertThat(firstPageIds).doesNotContainAnyElementsOf(secondPageIds);
    }

    @Test
    @DisplayName("정확히 페이지 크기만큼 데이터가 있으면 마지막 페이지로 표시된다")
    void exactPageSizeDataShowsAsLastPage() {
        // given: 정확히 5개의 지원서 생성
        createApplicationFormsWithTimeGap(5);

        // when: 첫 번째 페이지 조회
        Response response = ApplicationFormApiTestClient.findList(
                announcementId, null, null, null, ownerAuthSessionId);

        // then: 마지막 페이지임을 표시
        response.then()
                .body("meta.hasNext", equalTo(false))
                .body("meta.lastId", notNullValue())
                .body("meta.lastSubmittedAt", notNullValue());
    }

    @Test
    @DisplayName("빈 결과에서도 안정적으로 동작한다")
    void emptyResultHandledCorrectly() {
        // given: 지원서가 없음 (기본 설정에서 지원서 생성 안함)

        // when: 첫 번째 페이지 조회
        Response response = ApplicationFormApiTestClient.findList(
                announcementId, null, null, null, ownerAuthSessionId);

        // then: 빈 결과 반환
        response.then()
                .statusCode(200)
                .body("applicationForms", hasSize(0))
                .body("meta.hasNext", equalTo(false))
                .body("meta.lastId", nullValue())
                .body("meta.lastSubmittedAt", nullValue());
    }

    @Test
    @DisplayName("스터디장이 아닌 사용자는 지원서 목록을 조회할 수 없다")
    void nonOwnerCannotViewApplicationForms() {
        // given: 지원서 생성
        createApplicationFormsWithTimeGap(3);

        // given: 다른 사용자
        String otherUserSessionId = createUserAndLogin();

        // when & then: 권한 없는 사용자가 조회 시도
        ApplicationFormApiTestClient.findList(announcementId, null, null, null, otherUserSessionId)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("상태별 필터링이 정상 동작한다")
    void statusFilteringWorksCorrectly() {
        // given: 다양한 상태의 지원서 생성
        createApplicationFormsWithTimeGap(5);

        // when: SUBMITTED 상태만 필터링
        Response response = ApplicationFormApiTestClient.findList(
                announcementId, null, null, "SUBMITTED", ownerAuthSessionId);

        // then: SUBMITTED 상태의 지원서만 조회됨
        response.then()
                .statusCode(200)
                .body("applicationForms", hasSize(5));
    }

    @Test
    @DisplayName("유효하지 않은 커서 정보로 조회 시 적절히 처리된다")
    void invalidCursorHandledCorrectly() {
        // given: 지원서 생성
        createApplicationFormsWithTimeGap(3);

        // given: 존재하지 않는 ID와 미래 시간
        Long futureId        = Long.MAX_VALUE;
        LocalDateTime futureTime = LocalDateTime.of(2030, 1, 1, 0, 0);

        // when: 유효하지 않은 커서로 조회
        Response response = ApplicationFormApiTestClient.findList(
                announcementId, futureId, futureTime, null, ownerAuthSessionId);

        // then: 빈 결과나 적절한 에러 반환 (구현에 따라)
        response.then()
                .statusCode(200)
                .body("applicationForms", hasSize(0));
    }

    private String createUserAndLogin() {
        UserApiRequest.Register registerRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(registerRequest);

        AuthApiRequest.Login loginRequest = new AuthApiRequest.Login(
                registerRequest.loginId(), registerRequest.password());
        Response loginResponse = AuthApiTestClient.login(loginRequest);

        Cookie sessionCookie = loginResponse.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
        return sessionCookie.getValue();
    }

    private Long createStudyGroup(String authSessionId) {
        StudyGroupApiRequest.Create createRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response response = StudyGroupApiTestClient.create(createRequest, authSessionId);
        return response.jsonPath().getLong("studyGroup.id");
    }

    private Long createAnnouncement(Long studyGroupId, String authSessionId) {
        StudyAnnouncementApiRequest.Create request = StudyAnnouncementRequestFixture
                .generateValidCreateRequest(studyGroupId);
        Response response = StudyAnnouncementApiTestClient.create(request, authSessionId);
        return response.jsonPath().getLong("studyAnnouncement.id");
    }

    private void createApplicationFormsWithTimeGap(int count) {
        LocalDateTime baseTime = LocalDateTime.of(2024, 6, 1, 10, 0);
        systemClock.setFixedTime(baseTime);

        for (int i = 0; i < count; i++) {
            String applicantSessionId = createUserAndLogin();

            ApplicationFormApiRequest.Submit request = new ApplicationFormApiRequest.Submit(
                    String.format("지원서 내용 %d번째입니다. 열심히 하겠습니다!", i + 1));

            Response response = ApplicationFormApiTestClient.submit(announcementId, request, applicantSessionId);
            Long applicationFormId = response.jsonPath().getLong("applicationForm.id");
            applicationFormIds.add(applicationFormId);

            systemClock.advanceTime(Duration.ofMinutes(10));
        }
    }
}
