package dev.biddan.nubblev2.study.problem;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.study.group.StudyGroupApiTestClient;
import dev.biddan.nubblev2.study.group.StudyGroupRequestFixture;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.study.problem.controller.dto.ProblemApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("문제 삭제 테스트")
class ProblemDeleteTest extends AbstractIntegrationTest {

    private String authSessionId;
    private Long userId;
    private Long studyGroupId;

    @BeforeEach
    void setUp() {
        // given: 로그인된 사용자
        UserApiRequest.Register registerRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(registerRequest);

        AuthApiRequest.Login loginRequest = new AuthApiRequest.Login(
                registerRequest.loginId(),
                registerRequest.password()
        );

        Response loginResponse = AuthApiTestClient.login(loginRequest);
        userId = loginResponse.jsonPath().getLong("user.id");

        Cookie sessionCookie = loginResponse.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        authSessionId = sessionCookie.getValue();

        // given: 스터디 그룹 생성 (사용자가 그룹장이 됨)
        StudyGroupApiRequest.Create studyGroupRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response studyGroupResponse = StudyGroupApiTestClient.create(studyGroupRequest, authSessionId);
        studyGroupId = studyGroupResponse.jsonPath().getLong("studyGroup.id");
    }

    @Test
    @DisplayName("존재하지 않는 문제를 삭제하면 404 응답을 받는다")
    void deleteProblemWithNonExistentProblem() {
        // when: 존재하지 않는 문제 삭제
        Response response = ProblemApiTestClient.deleteProblem(studyGroupId, 999L, authSessionId);

        // then: 404 Not Found 응답
        response.then()
                .statusCode(404);
    }

    @Test
    @DisplayName("이미 삭제된 문제를 삭제하면 204 응답을 받는다")
    void deleteAlreadyDeletedProblem() {
        // given: 문제 생성 및 삭제
        ProblemApiRequest.Create createRequest = ProblemApiRequest.Create.builder()
                .title("두 수의 합")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();

        Response createResponse = ProblemApiTestClient.createProblem(studyGroupId, createRequest, authSessionId);
        Long problemId = createResponse.jsonPath().getLong("problem.id");

        ProblemApiTestClient.deleteProblem(studyGroupId, problemId, authSessionId);

        // when: 이미 삭제된 문제 재삭제
        Response response = ProblemApiTestClient.deleteProblem(studyGroupId, problemId, authSessionId);

        // then: 204 No Content 응답 (연속 삭제 처리 가능)
        response.then()
                .statusCode(204);
    }

    @Test
    @DisplayName("스터디 그룹 멤버가 아닌 사용자가 문제를 삭제하면 403 응답을 받는다")
    void deleteProblemAsNonMember() {
        // given: 문제 생성
        ProblemApiRequest.Create createRequest = ProblemApiRequest.Create.builder()
                .title("두 수의 합")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();

        Response createResponse = ProblemApiTestClient.createProblem(studyGroupId, createRequest, authSessionId);
        Long problemId = createResponse.jsonPath().getLong("problem.id");

        // given: 다른 사용자로 로그인
        UserApiRequest.Register anotherUserRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(anotherUserRequest);

        AuthApiRequest.Login anotherLoginRequest = new AuthApiRequest.Login(
                anotherUserRequest.loginId(),
                anotherUserRequest.password()
        );

        Response anotherLoginResponse = AuthApiTestClient.login(anotherLoginRequest);
        Cookie anotherSessionCookie = anotherLoginResponse.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        String anotherAuthSessionId = anotherSessionCookie.getValue();

        // when: 스터디 그룹 멤버가 아닌 사용자가 문제 삭제
        Response response = ProblemApiTestClient.deleteProblem(studyGroupId, problemId, anotherAuthSessionId);

        // then: 403 Forbidden 응답
        response.then()
                .statusCode(403);
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 문제를 삭제하면 401 응답을 받는다")
    void deleteProblemWithoutAuthentication() {
        // given: 문제 생성
        ProblemApiRequest.Create createRequest = ProblemApiRequest.Create.builder()
                .title("두 수의 합")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();

        Response createResponse = ProblemApiTestClient.createProblem(studyGroupId, createRequest, authSessionId);
        Long problemId = createResponse.jsonPath().getLong("problem.id");

        // when: 인증되지 않은 사용자가 문제 삭제
        Response response = ProblemApiTestClient.deleteProblem(studyGroupId, problemId, null);

        // then: 401 Unauthorized 응답
        response.then()
                .statusCode(401);
    }
}
