package dev.biddan.nubblev2.study.group;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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

@DisplayName("나의 스터디 그룹 목록 조회 테스트")
class MyStudyGroupListTest extends AbstractIntegrationTest {

    private String authSessionId;

    @BeforeEach
    void setUp() {
        // given: 사용자 1명 생성 및 로그인
        UserApiRequest.Register registerRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(registerRequest);

        AuthApiRequest.Login loginRequest = new AuthApiRequest.Login(
                registerRequest.loginId(), registerRequest.password());
        Response loginResponse = AuthApiTestClient.login(loginRequest);

        Cookie sessionCookie = loginResponse.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
        authSessionId = sessionCookie.getValue();
    }

    @Test
    @DisplayName("리더로 참여한 스터디 그룹을 조회할 수 있다")
    void getStudyGroupsAsLeader() {
        // given: 스터디 그룹 2개 생성
        StudyGroupApiRequest.Create request1 = createStudyGroupRequest("알고리즘 마스터 스터디");
        StudyGroupApiRequest.Create request2 = createStudyGroupRequest("백엔드 개발 스터디");

        StudyGroupApiTestClient.create(request1, authSessionId);
        StudyGroupApiTestClient.create(request2, authSessionId);

        // when: 나의 스터디 목록 조회
        Response response = StudyGroupApiTestClient.getMyStudyGroups(authSessionId);

        // then: 리더로 참여한 2개 스터디가 조회됨 (최신순)
        response.then()
                .statusCode(200)
                .body("studyGroups", hasSize(2))
                .body("studyGroups[0].role", equalTo("LEADER"))
                .body("studyGroups[0].studyGroup.name", equalTo("백엔드 개발 스터디"))
                .body("studyGroups[1].role", equalTo("LEADER"))
                .body("studyGroups[1].studyGroup.name", equalTo("알고리즘 마스터 스터디"));
    }

    @Test
    @DisplayName("참여한 스터디가 없는 경우 빈 목록을 반환한다")
    void getEmptyStudyGroupsWhenNotParticipating() {
        // given: 참여한 스터디가 없는 사용자
        // when: 나의 스터디 목록 조회
        Response response = StudyGroupApiTestClient.getMyStudyGroups(authSessionId);

        // then: 빈 목록 반환
        response.then()
                .statusCode(200)
                .body("studyGroups", hasSize(0));
    }

    private StudyGroupApiRequest.Create createStudyGroupRequest(String name) {
        return StudyGroupRequestFixture.generateValidCreateRequest()
                .toBuilder()
                .name(name)
                .build();
    }
}
