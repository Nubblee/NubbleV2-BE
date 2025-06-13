package dev.biddan.nubblev2.user.interest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.AuthApiTestClient;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import dev.biddan.nubblev2.user.interest.controller.dto.UserInterestApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("사용자 관심사 생성 테스트")
class UserInterestSetTest extends AbstractIntegrationTest {

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
    @DisplayName("처음 관심사를 설정할 수 있다")
    void setUserInterestForFirstTime() {
        // given: 관심사가 없는 사용자
        UserInterestApiRequest.Set request = UserInterestRequestFixture.generateValidSetRequest();

        // when: 관심사 설정
        Response response = UserInterestApiTestClient.set(request, authSessionId);

        // then: 200 OK (생성됨)
        response.then()
                .statusCode(200)
                .body("userInterest.userId", equalTo(userId.intValue()));
    }

    @Test
    @DisplayName("이미 설정된 관심사를 수정할 수 있다")
    void updateExistingUserInterest() {
        // given: 기존 관심사 설정
        UserInterestApiRequest.Set initialRequest = UserInterestRequestFixture.generateValidSetRequest();
        UserInterestApiTestClient.set(initialRequest, authSessionId);

        // given: 수정할 관심사
        UserInterestApiRequest.Set updateRequest = UserInterestApiRequest.Set.builder()
                .interestedLanguages(List.of("KOTLIN", "SWIFT"))
                .currentLevels(List.of("LV2", "LV3"))
                .preferredPlatforms(List.of("PROGRAMMERS", "BAEKJOON"))
                .build();

        // when: 관심사 수정
        Response response = UserInterestApiTestClient.set(updateRequest, authSessionId);

        // then: 200 OK (수정됨)
        response.then()
                .statusCode(200)
                .body("userInterest.interestedLanguages", hasItems("KOTLIN", "SWIFT"))
                .body("userInterest.currentLevels", hasItems("LV2", "LV3"))
                .body("userInterest.preferredPlatforms", hasItems("PROGRAMMERS", "BAEKJOON"));
    }
}
