package dev.biddan.nubblev2.study.group;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiRequest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("스터디 그룹 생성 테스트")
class StudyGroupCreateTest extends AbstractIntegrationTest {

    private String authSessionId;
    private Long userId;

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
    }

    @Test
    @DisplayName("유효한 정보로 스터디 그룹을 생성할 수 있다")
    void createStudyGroupWithValidData() {
        // given: 유효한 스터디 그룹 생성 요청
        System.out.printf("userId: %s%n", userId.toString());
        StudyGroupApiRequest.Create request = StudyGroupRequestFixture.generateValidCreateRequest();

        // when: 스터디 그룹 생성
        Response response = StudyGroupApiTestClient.create(request, authSessionId);

        // then: 201 Created 응답과 스터디 그룹 정보 반환
        response.then()
                .statusCode(201)
                .body("studyGroup.id", notNullValue())
                .body("studyGroup.name", equalTo(request.name()))
                .body("studyGroup.description", equalTo(request.description()))
                .body("studyGroup.capacity", equalTo(request.capacity()))
                .body("studyGroup.startDate", equalTo(request.startDate().toString()))
                .body("studyGroup.endDate", equalTo(request.endDate().toString()))
                .body("studyGroup.languages", hasItems(request.languages().toArray()))
                .body("studyGroup.mainLanguage", equalTo(request.mainLanguage()))
                .body("studyGroup.difficultyLevels", hasItems(request.difficultyLevels().toArray()))
                .body("studyGroup.problemPlatforms", hasItems(request.problemPlatforms().toArray()))
                .body("studyGroup.meetingType", equalTo(request.meetingType()))
                .body("studyGroup.meetingRegion", equalTo(request.meetingRegion()))
                .body("studyGroup.mainMeetingDays", hasItems(request.mainMeetingDays().toArray()))
                .body("studyGroup.creator.id", equalTo(userId.intValue()))
                .body("studyGroup.creator.nickname", notNullValue());

        // then: DB에 저장 확인
        Long studyGroupId = response.jsonPath().getLong("studyGroup.id");
        assertThat(studyGroupRepository.findById(studyGroupId)).isPresent();
    }
}
