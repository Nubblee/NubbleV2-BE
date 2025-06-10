package dev.biddan.nubblev2.auth;

import static dev.biddan.nubblev2.user.UserRequestFixture.generateValidUserRegisterRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.auth.domain.LoginLog;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.Cookie.SameSite;

@DisplayName("로그인 테스트")
class LoginTest extends AbstractIntegrationTest {

    private UserApiRequest.Register registerRequest;
    private AuthApiRequest.Login loginRequest;

    @BeforeEach
    void setUp() {
        // given: 회원가입된 사용자
        registerRequest = generateValidUserRegisterRequest();
        UserApiTestClient.register(registerRequest);

        loginRequest = new AuthApiRequest.Login(
                registerRequest.loginId(),
                registerRequest.password()
        );
    }

    @Nested
    @DisplayName("정상 로그인")
    class SuccessfulLogin {

        @Test
        @DisplayName("올바른 인증정보로 로그인하면 유저 정보와 세션 쿠키를 반환한다")
        void loginWithValidCredentials() {
            // when: 로그인 시도
            Response response = AuthApiTestClient.login(loginRequest);

            // then: 200 OK와 유저 정보 반환
            response.then()
                    .statusCode(200)
                    .body("user.id", notNullValue())
                    .body("user.loginId", equalTo(registerRequest.loginId()))
                    .body("user.nickname", equalTo(registerRequest.nickname()))
                    .body("user.password", nullValue()); // 비밀번호는 반환하지 않음

            // then: 세션 쿠키 검증
            Cookie sessionCookie = response.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
            assertThat(sessionCookie).isNotNull();
            assertThat(sessionCookie.getValue()).isNotBlank();
            assertThat(sessionCookie.isHttpOnly()).isTrue(); // XSS 방지
            assertThat(sessionCookie.getPath()).isEqualTo("/");
            assertThat(sessionCookie.getSameSite()).isEqualTo(SameSite.LAX.toString()); // CSRF 방지 (크롬 기본값)
        }

        @Test
        @DisplayName("동일한 사용자가 여러 번 로그인하면 각각 다른 세션을 발급한다")
        void multipleLoginsShouldCreateDifferentSessions() {
            // when: 두 번 로그인
            Response firstLogin = AuthApiTestClient.login(loginRequest);
            Response secondLogin = AuthApiTestClient.login(loginRequest);

            // then: 각각 다른 세션 ID 발급
            String firstSessionId = firstLogin.getCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
            String secondSessionId = secondLogin.getCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);

            assertThat(firstSessionId).isNotEqualTo(secondSessionId);
        }
    }

    @Nested
    @DisplayName("로그인 실패")
    class FailedLogin {

        @Test
        @DisplayName("존재하지 않는 아이디로 로그인하면 401을 반환한다")
        void loginWithNonExistentLoginId() {
            // given: 존재하지 않는 아이디 요청
            AuthApiRequest.Login nonExistentIdRequest = new AuthApiRequest.Login(
                    "nonexistent_user",
                    "password123!"
            );

            // when & then
            AuthApiTestClient.login(nonExistentIdRequest)
                    .then()
                    .statusCode(401)
                    .body("detail", containsString("아이디 또는 비밀번호가 올바르지 않습니다"));
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인하면 401을 반환한다")
        void loginWithWrongPassword() {
            // given: 잘못된 비밀번호 요청
            AuthApiRequest.Login incorrectPasswordRequest = new AuthApiRequest.Login(
                    registerRequest.loginId(),
                    "wrongpassword!"
            );

            // when & then
            AuthApiTestClient.login(incorrectPasswordRequest)
                    .then()
                    .statusCode(401)
                    .body("detail", containsString("아이디 또는 비밀번호가 올바르지 않습니다"));
        }
    }

    @Nested
    @DisplayName("로그인 로그")
    class LoginLogTest {

        @Test
        @DisplayName("로그인 시도시 로그인 로그를 기록한다")
        void loginAttemptsAreLogged() {
            // given: 초기 로그 개수 확인
            long initialLogCount = loginLogRepository.count();

            // when: 실패한 로그인 시도
            AuthApiTestClient.login(new AuthApiRequest.Login(
                    registerRequest.loginId(),
                    "wrongpassword"
            ));

            // then: 로그인 실패 로그 검증
            List<LoginLog> failureLogs = loginLogRepository.findAll();
            assertThat(failureLogs).hasSize((int) initialLogCount + 1);

            LoginLog failureLog = failureLogs.get(failureLogs.size() - 1);
            assertThat(failureLog.getLoginId()).isEqualTo(registerRequest.loginId());
            assertThat(failureLog.getLoginStatus()).isEqualTo(LoginLog.LoginStatus.FAILURE);
            assertThat(failureLog.getFailureReason()).contains("비밀번호가 일치하지 않습니다");

            // when: 성공한 로그인 시도
            AuthApiTestClient.login(loginRequest);

            // then: 로그인 성공 로그 검증
            List<LoginLog> allLogs = loginLogRepository.findAll();
            assertThat(allLogs).hasSize((int) initialLogCount + 2);

            LoginLog successLog = allLogs.get(allLogs.size() - 1);
            assertThat(successLog.getLoginId()).isEqualTo(registerRequest.loginId());
            assertThat(successLog.getLoginStatus()).isEqualTo(LoginLog.LoginStatus.SUCCESS);
            assertThat(successLog.getFailureReason()).isNull();
            assertThat(successLog.getAuthSessionId()).isNotNull();
        }
    }
}
