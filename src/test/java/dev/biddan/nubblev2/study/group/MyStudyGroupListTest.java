package dev.biddan.nubblev2.study.group;

import static dev.biddan.nubblev2.http.AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME;
import static org.hamcrest.Matchers.*;
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
    @DisplayName("참여한 스터디가 없는 경우 빈 목록을 반환한다")
    void getEmptyStudyGroupsWhenNotParticipating() {
        // given: 참여한 스터디가 없는 사용자
        // when: 나의 스터디 목록 조회
        Response response = StudyGroupApiTestClient.getMyStudyGroups(authSessionId);

        // then: 빈 목록 반환
        response.then()
                .statusCode(200)
                .body("studyGroups", hasSize(0))
                // PageMeta 검증 (빈 목록일 때)
                .body("meta.page", equalTo(1))
                .body("meta.totalPages", equalTo(1))
                .body("meta.totalSize", equalTo(0))
                .body("meta.hasNext", equalTo(false))
                .body("meta.hasPrevious", equalTo(false));
    }

    @Test
    @DisplayName("스터디 그룹의 상세 정보가 올바르게 반환된다")
    void getStudyGroupsWithDetailedInfo() {
        // given: 스터디 그룹 1개 생성
        StudyGroupApiRequest.Create request = createStudyGroupRequest("알고리즘 마스터 스터디");
        StudyGroupApiTestClient.create(request, authSessionId);

        // when: 나의 스터디 목록 조회
        Response response = StudyGroupApiTestClient.getMyStudyGroups(authSessionId);

        // then: 스터디 그룹의 상세 정보가 포함됨
        response.then()
                .statusCode(200)
                .body("studyGroups", hasSize(1))
                .body("studyGroups[0].name", equalTo("알고리즘 마스터 스터디"))
                .body("studyGroups[0].id", notNullValue())
                .body("studyGroups[0].mainLanguage", notNullValue())
                .body("studyGroups[0].capacity", notNullValue())
                .body("studyGroups[0].meetingType", notNullValue())
                .body("studyGroups[0].meetingRegion", notNullValue())
                .body("studyGroups[0].difficultyLevels", notNullValue())
                .body("studyGroups[0].mainMeetingDays", notNullValue())
                .body("studyGroups[0].meta.currentMemberCount", notNullValue())
                // PageMeta 검증
                .body("meta.page", equalTo(1))
                .body("meta.totalPages", equalTo(1))
                .body("meta.totalSize", equalTo(1))
                .body("meta.hasNext", equalTo(false))
                .body("meta.hasPrevious", equalTo(false));
    }

    private StudyGroupApiRequest.Create createStudyGroupRequest(String name) {
        return StudyGroupRequestFixture.generateValidCreateRequest()
                .toBuilder()
                .name(name)
                .build();
    }
}
