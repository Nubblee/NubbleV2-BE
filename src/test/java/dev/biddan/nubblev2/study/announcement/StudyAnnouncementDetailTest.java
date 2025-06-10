package dev.biddan.nubblev2.study.announcement;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import dev.biddan.nubblev2.study.group.StudyGroupApiTestClient;
import dev.biddan.nubblev2.study.group.StudyGroupRequestFixture;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 공고 상세 조회 테스트")
class StudyAnnouncementDetailTest extends AbstractIntegrationTest {

    private Long studyGroupId;
    private Long announcementId;
    private StudyAnnouncementApiRequest.Create originalRequest;

    @BeforeEach
    void setUp() {
        // given: 스터디 그룹 소유자 회원가입 및 로그인
        UserApiRequest.Register ownerRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(ownerRegisterRequest);

        AuthApiRequest.Login ownerLoginRequest = new AuthApiRequest.Login(
                ownerRegisterRequest.loginId(),
                ownerRegisterRequest.password()
        );

        Response ownerLoginResponse = AuthApiTestClient.login(ownerLoginRequest);
        Cookie ownerSessionCookie = ownerLoginResponse.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
        String ownerAuthSessionId = ownerSessionCookie.getValue();

        // given: 스터디 그룹 생성
        StudyGroupApiRequest.Create createRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response createResponse = StudyGroupApiTestClient.create(createRequest, ownerAuthSessionId);
        studyGroupId = createResponse.jsonPath().getLong("studyGroup.id");

        // given: 스터디 공고 생성
        originalRequest = StudyAnnouncementRequestFixture
                .generateValidCreateRequest(studyGroupId);
        Response announcementResponse = StudyAnnouncementApiTestClient.create(originalRequest, ownerAuthSessionId);
        announcementId = announcementResponse.jsonPath().getLong("studyAnnouncement.id");
    }

    @Test
    @DisplayName("인증 없이도 공고 상세 정보를 조회할 수 있다")
    void getAnnouncementDetailWithoutAuth() {
        // when: 인증 없이 공고 상세 조회
        Response response = StudyAnnouncementApiTestClient.findById(announcementId);

        // then: 200 OK와 공고 상세 정보 반환
        response.then()
                .statusCode(200)
                .body("studyAnnouncement.id", equalTo(announcementId.intValue()))
                .body("studyAnnouncement.studyGroupId", equalTo(studyGroupId.intValue()))
                .body("studyAnnouncement.title", equalTo(originalRequest.title()))
                .body("studyAnnouncement.description", equalTo(originalRequest.description()))
                .body("studyAnnouncement.recruitCapacity", equalTo(originalRequest.recruitCapacity()))
                .body("studyAnnouncement.endDate", equalTo(originalRequest.endDate().toString()))
                .body("studyAnnouncement.status", equalTo("RECRUITING"))
                .body("studyAnnouncement.closedReason", nullValue())
                .body("studyAnnouncement.createdAt", notNullValue())
                .body("studyAnnouncement.closedAt", nullValue())
                .body("studyAnnouncement.applicationForm", equalTo(originalRequest.applicationFormContent()));
    }

    @Test
    @DisplayName("존재하지 않는 공고 ID로 조회하면 404를 반환한다")
    void getAnnouncementDetailWithNonExistentId() {
        // given: 존재하지 않는 공고 ID
        Long nonExistentId = 99999L;

        // when & then: 존재하지 않는 공고 조회
        StudyAnnouncementApiTestClient.findById(nonExistentId)
                .then()
                .statusCode(404)
                .body("detail", containsString("존재하지 않는 모집 공고입니다"));
    }
}
