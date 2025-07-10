package dev.biddan.nubblev2.study.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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

@DisplayName("문제 생성 테스트")
class ProblemCreateTest extends AbstractIntegrationTest {

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
    @DisplayName("스터디 그룹장이 문제를 성공적으로 생성할 수 있다")
    void createProblemAsLeader() {
        // given: 유효한 문제 생성 요청
        ProblemApiRequest.Create request = ProblemApiRequest.Create.builder()
                .title("두 수의 합")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();

        // when: 문제 생성
        Response response = ProblemApiTestClient.createProblem(studyGroupId, request, authSessionId);

        // then: 201 Created 응답과 문제 정보 반환
        response.then()
                .statusCode(201)
                .body("problem.id", notNullValue())
                .body("problem.title", equalTo(request.title()))
                .body("problem.url", equalTo(request.url()))
                .body("problem.date", equalTo(request.date().toString()))
                .body("problem.createdBy", equalTo(userId.intValue()))
                .body("problem.studyGroupId", equalTo(studyGroupId.intValue()))
                .body("problem.createdAt", notNullValue());

        // then: DB에 저장 확인
        Long problemId = response.jsonPath().getLong("problem.id");
        assertThat(problemRepository.findById(problemId)).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 스터디 그룹에 문제를 생성하면 404 응답을 받는다")
    void createProblemWithNonExistentStudyGroup() {
        // given: 유효한 문제 생성 요청
        ProblemApiRequest.Create request = ProblemApiRequest.Create.builder()
                .title("두 수의 합")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();

        // when: 존재하지 않는 스터디 그룹에 문제 생성
        Response response = ProblemApiTestClient.createProblem(999L, request, authSessionId);

        // then: 404 Not Found 응답
        response.then()
                .statusCode(404);
    }

    @Test
    @DisplayName("스터디 그룹 멤버가 아닌 사용자가 문제를 생성하면 403 응답을 받는다")
    void createProblemAsNonMember() {
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

        // given: 유효한 문제 생성 요청
        ProblemApiRequest.Create request = ProblemApiRequest.Create.builder()
                .title("두 수의 합")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();

        // when: 스터디 그룹 멤버가 아닌 사용자가 문제 생성
        Response response = ProblemApiTestClient.createProblem(studyGroupId, request, anotherAuthSessionId);

        // then: 403 Forbidden 응답
        response.then()
                .statusCode(403);
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 문제를 생성하면 401 응답을 받는다")
    void createProblemWithoutAuthentication() {
        // given: 유효한 문제 생성 요청
        ProblemApiRequest.Create request = ProblemApiRequest.Create.builder()
                .title("두 수의 합")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();

        // when: 인증되지 않은 사용자가 문제 생성
        Response response = ProblemApiTestClient.createProblem(studyGroupId, request, null);

        // then: 401 Unauthorized 응답
        response.then()
                .statusCode(401);
    }
}