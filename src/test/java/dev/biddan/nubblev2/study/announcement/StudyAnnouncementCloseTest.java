package dev.biddan.nubblev2.study.announcement;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement;
import dev.biddan.nubblev2.study.announcement.domain.StudyAnnouncement.AnnouncementStatus;
import dev.biddan.nubblev2.study.group.StudyGroupApiTestClient;
import dev.biddan.nubblev2.study.group.StudyGroupRequestFixture;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 공고 마감 테스트")
class StudyAnnouncementCloseTest extends AbstractIntegrationTest {

    private String ownerAuthSessionId;
    private String otherUserAuthSessionId;
    private Long announcementId;

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
        ownerAuthSessionId = ownerSessionCookie.getValue();

        // given: 스터디 그룹 생성
        StudyGroupApiRequest.Create createRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response createResponse = StudyGroupApiTestClient.create(createRequest, ownerAuthSessionId);
        Long studyGroupId = createResponse.jsonPath().getLong("studyGroup.id");

        // given: 스터디 공고 생성
        StudyAnnouncementApiRequest.Create announcementRequest = StudyAnnouncementRequestFixture
                .generateValidCreateRequest(studyGroupId);
        Response announcementResponse = StudyAnnouncementApiTestClient.create(announcementRequest, ownerAuthSessionId);
        announcementId = announcementResponse.jsonPath().getLong("studyAnnouncement.id");

        // given: 다른 사용자 회원가입 및 로그인
        UserApiRequest.Register otherRegisterRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(otherRegisterRequest);

        AuthApiRequest.Login otherLoginRequest = new AuthApiRequest.Login(
                otherRegisterRequest.loginId(),
                otherRegisterRequest.password()
        );

        Response otherLoginResponse = AuthApiTestClient.login(otherLoginRequest);
        Cookie otherSessionCookie = otherLoginResponse.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
        otherUserAuthSessionId = otherSessionCookie.getValue();
    }

    @Test
    @DisplayName("스터디장은 모집중인 공고를 마감할 수 있다")
    void closeRecruitingAnnouncementByOwner() {
        // given: 특정 시간으로 고정
        LocalDateTime closeTime = LocalDateTime.of(2024, 6, 15, 10, 30);
        systemClock.setFixedTime(closeTime);

        // when: 공고 마감
        Response response = StudyAnnouncementApiTestClient.close(announcementId, ownerAuthSessionId);

        // then: 200 OK와 마감된 공고 정보 반환
        response.then()
                .statusCode(200)
                .body("studyAnnouncement.id", equalTo(announcementId.intValue()))
                .body("studyAnnouncement.status", equalTo("CLOSED"))
                .body("studyAnnouncement.closedReason", equalTo("MANUAL"))
                .body("studyAnnouncement.closedAt", notNullValue());
    }

    @Test
    @DisplayName("스터디장이 아닌 사용자가 공고를 마감하려고 하면 403을 반환한다")
    void closeAnnouncementByNonOwnerShouldReturn403() {
        // when & then: 다른 사용자가 공고 마감 시도
        StudyAnnouncementApiTestClient.close(announcementId, otherUserAuthSessionId)
                .then()
                .statusCode(403)
                .body("detail", containsString("스터디 공고를 마감할 권한이 없습니다"));

        // then: DB에서 상태가 변경되지 않았음을 확인
        StudyAnnouncement announcement = studyAnnouncementRepository.findById(announcementId).orElseThrow();
        assertThat(announcement.getStatus()).isEqualTo(AnnouncementStatus.RECRUITING);
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 공고를 마감하려고 하면 401을 반환한다")
    void closeAnnouncementWithoutAuthShouldReturn401() {
        // when & then: 인증 없이 공고 마감 시도
        StudyAnnouncementApiTestClient.close(announcementId, null)
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("이미 마감된 공고를 다시 마감하려고 하면 409을 반환한다")
    void closeAlreadyClosedAnnouncementShouldReturn400() {
        // given: 공고를 먼저 마감
        StudyAnnouncementApiTestClient.close(announcementId, ownerAuthSessionId);

        // when & then: 다시 마감 시도
        StudyAnnouncementApiTestClient.close(announcementId, ownerAuthSessionId)
                .then()
                .statusCode(409)
                .body("detail", containsString("이미 마감된 공고입니다"));
    }

    @Test
    @DisplayName("존재하지 않는 공고를 마감하려고 하면 404를 반환한다")
    void closeNonExistentAnnouncementShouldReturn404() {
        // given: 존재하지 않는 공고 ID
        Long nonExistentAnnouncementId = 99999L;

        // when & then: 존재하지 않는 공고 마감 시도
        StudyAnnouncementApiTestClient.close(nonExistentAnnouncementId, ownerAuthSessionId)
                .then()
                .statusCode(404)
                .body("detail", containsString("존재하지 않는 모집 공고입니다"));
    }
}
