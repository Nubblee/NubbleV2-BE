package dev.biddan.nubblev2.study.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.study.group.StudyGroupApiTestClient;
import dev.biddan.nubblev2.study.group.StudyGroupRequestFixture;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.study.problem.controller.dto.ProblemApiRequest;
import dev.biddan.nubblev2.study.problem.domain.Problem;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("문제 목록 조회 테스트")
class ProblemListTest extends AbstractIntegrationTest {

    private String authSessionId;
    private Long studyGroupId;
    private Long anotherStudyGroupId;

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

        Cookie sessionCookie = loginResponse.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        authSessionId = sessionCookie.getValue();

        // given: 첫 번째 스터디 그룹 생성
        StudyGroupApiRequest.Create studyGroupRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response studyGroupResponse = StudyGroupApiTestClient.create(studyGroupRequest, authSessionId);
        studyGroupId = studyGroupResponse.jsonPath().getLong("studyGroup.id");

        // given: 두 번째 스터디 그룹 생성
        StudyGroupApiRequest.Create anotherStudyGroupRequest = StudyGroupRequestFixture.generateValidCreateRequest();
        Response anotherStudyGroupResponse = StudyGroupApiTestClient.create(anotherStudyGroupRequest, authSessionId);
        anotherStudyGroupId = anotherStudyGroupResponse.jsonPath().getLong("studyGroup.id");
    }

    @Test
    @DisplayName("문제가 없는 스터디 그룹에서 빈 목록을 반환한다")
    void getProblemsWhenEmpty() {
        // when: 문제 목록 조회
        Response response = ProblemApiTestClient.getProblems(studyGroupId, 1, 10);

        // then: 200 OK 응답과 빈 목록 반환
        response.then()
                .statusCode(200)
                .body("problems", hasSize(0))
                .body("meta.page", equalTo(1))
                .body("meta.totalPages", equalTo(0))
                .body("meta.totalSize", equalTo(0))
                .body("meta.hasNext", equalTo(false))
                .body("meta.hasPrevious", equalTo(false));
    }

    @Test
    @DisplayName("삭제된 문제는 목록에 포함되지 않는다")
    void getProblemsExcludesDeletedProblems() {
        // given: 문제 생성
        ProblemApiRequest.Create createRequest1 = ProblemApiRequest.Create.builder()
                .title("활성 문제")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();

        ProblemApiRequest.Create createRequest2 = ProblemApiRequest.Create.builder()
                .title("삭제될 문제")
                .url("https://programmers.co.kr/learn/courses/30/lessons/2")
                .date(LocalDate.now())
                .build();

        Response createResponse1 = ProblemApiTestClient.createProblem(studyGroupId, createRequest1, authSessionId);
        Response createResponse2 = ProblemApiTestClient.createProblem(studyGroupId, createRequest2, authSessionId);

        Long problemId2 = createResponse2.jsonPath().getLong("problem.id");

        // given: 두 번째 문제 삭제
        ProblemApiTestClient.deleteProblem(studyGroupId, problemId2, authSessionId);

        // when: 문제 목록 조회
        Response response = ProblemApiTestClient.getProblems(studyGroupId, 1, 10);

        // then: 활성 문제만 반환
        response.then()
                .statusCode(200)
                .body("problems", hasSize(1))
                .body("problems[0].title", equalTo("활성 문제"))
                .body("meta.totalSize", equalTo(1));
    }

    @Test
    @DisplayName("여러 문제가 최신순으로 정렬되어 반환된다")
    void getProblemsOrderedByCreatedAtDesc() {
        // given: 시간을 고정하여 순서 보장
        LocalDateTime baseTime = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        systemClock.setFixedTime(baseTime);

        // given: 첫 번째 문제 생성
        ProblemApiRequest.Create createRequest1 = ProblemApiRequest.Create.builder()
                .title("첫 번째 문제")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();
        ProblemApiTestClient.createProblem(studyGroupId, createRequest1, authSessionId);

        // given: 시간을 1시간 후로 이동
        systemClock.setFixedTime(baseTime.plusHours(1));

        // given: 두 번째 문제 생성
        ProblemApiRequest.Create createRequest2 = ProblemApiRequest.Create.builder()
                .title("두 번째 문제")
                .url("https://programmers.co.kr/learn/courses/30/lessons/2")
                .date(LocalDate.now())
                .build();
        ProblemApiTestClient.createProblem(studyGroupId, createRequest2, authSessionId);

        // given: 시간을 2시간 후로 이동
        systemClock.setFixedTime(baseTime.plusHours(2));

        // given: 세 번째 문제 생성
        ProblemApiRequest.Create createRequest3 = ProblemApiRequest.Create.builder()
                .title("세 번째 문제")
                .url("https://programmers.co.kr/learn/courses/30/lessons/3")
                .date(LocalDate.now())
                .build();
        ProblemApiTestClient.createProblem(studyGroupId, createRequest3, authSessionId);

        // when: 문제 목록 조회
        Response response = ProblemApiTestClient.getProblems(studyGroupId, 1, 10);

        // then: 최신순으로 정렬된 문제 목록 반환
        response.then()
                .statusCode(200)
                .body("problems", hasSize(3))
                .body("problems[0].title", equalTo("세 번째 문제"))  // 가장 최신
                .body("problems[1].title", equalTo("두 번째 문제"))
                .body("problems[2].title", equalTo("첫 번째 문제"))  // 가장 오래된
                .body("meta.totalSize", equalTo(3));
    }

    @Test
    @DisplayName("다른 스터디 그룹의 문제는 조회되지 않는다")
    void getProblemsIsolatedByStudyGroup() {
        // given: 첫 번째 스터디 그룹에 문제 생성
        ProblemApiRequest.Create createRequest1 = ProblemApiRequest.Create.builder()
                .title("첫 번째 그룹 문제")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();
        ProblemApiTestClient.createProblem(studyGroupId, createRequest1, authSessionId);

        // given: 두 번째 스터디 그룹에 문제 생성
        ProblemApiRequest.Create createRequest2 = ProblemApiRequest.Create.builder()
                .title("두 번째 그룹 문제")
                .url("https://programmers.co.kr/learn/courses/30/lessons/2")
                .date(LocalDate.now())
                .build();
        ProblemApiTestClient.createProblem(anotherStudyGroupId, createRequest2, authSessionId);

        // when: 첫 번째 스터디 그룹의 문제 목록 조회
        Response response1 = ProblemApiTestClient.getProblems(studyGroupId, 1, 10);

        // then: 첫 번째 그룹의 문제만 반환
        response1.then()
                .statusCode(200)
                .body("problems", hasSize(1))
                .body("problems[0].title", equalTo("첫 번째 그룹 문제"));

        // when: 두 번째 스터디 그룹의 문제 목록 조회
        Response response2 = ProblemApiTestClient.getProblems(anotherStudyGroupId, 1, 10);

        // then: 두 번째 그룹의 문제만 반환
        response2.then()
                .statusCode(200)
                .body("problems", hasSize(1))
                .body("problems[0].title", equalTo("두 번째 그룹 문제"));
    }

    @Test
    @DisplayName("page와 limit 파라미터가 정상적으로 동작한다")
    void getProblemsWithPageAndLimit() {
        // given: 5개의 문제 생성
        for (int i = 1; i <= 5; i++) {
            ProblemApiRequest.Create createRequest = ProblemApiRequest.Create.builder()
                    .title("문제 " + i)
                    .url("https://programmers.co.kr/learn/courses/30/lessons/" + i)
                    .date(LocalDate.now())
                    .build();
            ProblemApiTestClient.createProblem(studyGroupId, createRequest, authSessionId);
            
            // 각 문제 생성 간 시간 간격을 두어 순서 보장
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // when: 첫 번째 페이지 조회 (page=1, limit=2)
        Response response1 = ProblemApiTestClient.getProblems(studyGroupId, 1, 2);

        // then: 최신 2개 문제 반환
        response1.then()
                .statusCode(200)
                .body("problems", hasSize(2))
                .body("problems[0].title", equalTo("문제 5"))  // 가장 최신
                .body("problems[1].title", equalTo("문제 4"))
                .body("meta.page", equalTo(1))
                .body("meta.totalSize", equalTo(5));

        // when: 두 번째 페이지 조회 (page=2, limit=2)
        Response response2 = ProblemApiTestClient.getProblems(studyGroupId, 2, 2);

        // then: 다음 2개 문제 반환
        response2.then()
                .statusCode(200)
                .body("problems", hasSize(2))
                .body("problems[0].title", equalTo("문제 3"))
                .body("problems[1].title", equalTo("문제 2"))
                .body("meta.page", equalTo(2));

        // when: 세 번째 페이지 조회 (page=3, limit=2)
        Response response3 = ProblemApiTestClient.getProblems(studyGroupId, 3, 2);

        // then: 마지막 1개 문제 반환
        response3.then()
                .statusCode(200)
                .body("problems", hasSize(1))
                .body("problems[0].title", equalTo("문제 1"))  // 가장 오래된
                .body("meta.page", equalTo(3));
    }

    @Test
    @DisplayName("존재하지 않는 스터디 그룹 ID로 조회하면 404 응답을 받는다")
    void getProblemsWithNonExistentStudyGroup() {
        // when: 존재하지 않는 스터디 그룹의 문제 목록 조회
        Response response = ProblemApiTestClient.getProblems(999L, 1, 10);

        // then: 404 Not Found 응답
        response.then()
                .statusCode(404);
    }

    @Test
    @DisplayName("limit 파라미터가 최대값을 초과하면 100으로 제한된다")
    void getProblemsWithLimitExceedsMaximum() {
        // given: 문제 생성
        ProblemApiRequest.Create createRequest = ProblemApiRequest.Create.builder()
                .title("테스트 문제")
                .url("https://programmers.co.kr/learn/courses/30/lessons/1")
                .date(LocalDate.now())
                .build();
        ProblemApiTestClient.createProblem(studyGroupId, createRequest, authSessionId);

        // when: limit을 200으로 설정하여 조회
        Response response = ProblemApiTestClient.getProblems(studyGroupId, 1, 200);

        // then: 정상적으로 응답 (내부적으로 100으로 제한됨)
        response.then()
                .statusCode(200)
                .body("problems", hasSize(1));
    }
}
