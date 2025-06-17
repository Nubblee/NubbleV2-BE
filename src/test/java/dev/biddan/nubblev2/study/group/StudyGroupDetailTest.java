package dev.biddan.nubblev2.study.group;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 그룹 상세 조회 테스트")
class StudyGroupDetailTest extends AbstractIntegrationTest {

    private Long studyGroupId;
    private StudyGroupApiRequest.Create originalRequest;

    @BeforeEach
    void setUp() {
        // given: 스터디 그룹 생성자 회원가입 및 로그인
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
        originalRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response createResponse = StudyGroupApiTestClient.create(originalRequest, ownerAuthSessionId);
        studyGroupId = createResponse.jsonPath().getLong("studyGroup.id");
    }

    @Test
    @DisplayName("인증 없이도 스터디 그룹 상세 정보를 조회할 수 있다")
    void getStudyGroupDetailWithoutAuth() {
        // when: 인증 없이 스터디 그룹 상세 조회
        Response response = StudyGroupApiTestClient.getById(studyGroupId);

        // then: 200 OK와 스터디 그룹 상세 정보 반환
        response.then()
                .statusCode(200)
                .body("studyGroup.id", equalTo(studyGroupId.intValue()))
                .body("studyGroup.name", equalTo(originalRequest.name()))
                .body("studyGroup.description", equalTo(originalRequest.description()))
                .body("studyGroup.capacity", equalTo(originalRequest.capacity()))
                .body("studyGroup.languages", hasItems(originalRequest.languages().toArray()))
                .body("studyGroup.mainLanguage", equalTo(originalRequest.mainLanguage()))
                .body("studyGroup.difficultyLevels", hasItems(originalRequest.difficultyLevels().toArray()))
                .body("studyGroup.problemPlatforms", hasItems(originalRequest.problemPlatforms().toArray()))
                .body("studyGroup.meetingType", equalTo(originalRequest.meetingType()))
                .body("studyGroup.meetingRegion", equalTo(originalRequest.meetingRegion()))
                .body("studyGroup.mainMeetingDays", hasItems(originalRequest.mainMeetingDays().toArray()));
    }

    @Test
    @DisplayName("존재하지 않는 스터디 그룹 ID로 조회하면 404를 반환한다")
    void getStudyGroupDetailWithNonExistentId() {
        // given: 존재하지 않는 스터디 그룹 ID
        Long nonExistentId = 99999L;

        // when & then: 존재하지 않는 스터디 그룹 조회
        StudyGroupApiTestClient.getById(nonExistentId)
                .then()
                .statusCode(404)
                .body("detail", containsString("존재하지 않는 스터디 그룹입니다"));
    }
}
