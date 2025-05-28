package dev.biddan.nubblev2.auth;

import static dev.biddan.nubblev2.user.UserRequestFixture.generateValidUserRegisterRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.controller.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("로그인 테스트")
class LoginTest extends AbstractIntegrationTest {

    private static final String AUTH_SESSION_COOKIE_NAME = "auth-session";

    private UserApiRequest.Register registerRequest;
    private UserApiRequest.Login loginRequest;

    @BeforeEach
    void setUp() {
        // given: 회원가입된 사용자
        registerRequest = generateValidUserRegisterRequest();
        UserApiTestClient.register(registerRequest);

        loginRequest = new UserApiRequest.Login(
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
            Response response = UserApiTestClient.login(loginRequest);

            // then: 200 OK와 유저 정보 반환
            response.then()
                    .statusCode(200)
                    .body("user.id", notNullValue())
                    .body("user.loginId", equalTo(registerRequest.loginId()))
                    .body("user.nickname", equalTo(registerRequest.nickname()))
                    .body("user.password", nullValue()); // 비밀번호는 반환하지 않음

            // then: 세션 쿠키 검증
            Cookie sessionCookie = response.getDetailedCookie(AUTH_SESSION_COOKIE_NAME);
            assertThat(sessionCookie).isNotNull();
            assertThat(sessionCookie.getValue()).isNotBlank();
            assertThat(sessionCookie.isHttpOnly()).isTrue(); // XSS 방지
            assertThat(sessionCookie.getPath()).isEqualTo("/");
            assertThat(sessionCookie.getSameSite()).isEqualTo("Lax"); // CSRF 방지 (크롬 기본값)
        }

        @Test
        @DisplayName("동일한 사용자가 여러 번 로그인하면 각각 다른 세션을 발급한다")
        void multipleLoginsShouldCreateDifferentSessions() {
            // when: 두 번 로그인
            Response firstLogin = UserApiTestClient.login(loginRequest);
            Response secondLogin = UserApiTestClient.login(loginRequest);

            // then: 각각 다른 세션 ID 발급
            String firstSessionId = firstLogin.getCookie(AUTH_SESSION_COOKIE_NAME);
            String secondSessionId = secondLogin.getCookie(AUTH_SESSION_COOKIE_NAME);

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
            UserApiRequest.Login nonExistentIdRequest = new UserApiRequest.Login(
                    "nonexistent_user_" + UUID.randomUUID(),
                    "password123!"
            );

            // when & then
            UserApiTestClient.login(nonExistentIdRequest)
                    .then()
                    .statusCode(401)
                    .body("detail", containsString("아이디 또는 비밀번호가 올바르지 않습니다"))
                    .cookie(AUTH_SESSION_COOKIE_NAME, nullValue()); // 쿠키 발급하지 않음
        }

        @Test
        @DisplayName("잘못된 비밀번호로 로그인하면 401을 반환한다")
        void loginWithWrongPassword() {
            // given: 잘못된 비밀번호 요청
            UserApiRequest.Login incorrectPasswordRequest = new UserApiRequest.Login(
                    registerRequest.loginId(),
                    "wrong_password!"
            );

            // when & then
            UserApiTestClient.login(incorrectPasswordRequest)
                    .then()
                    .statusCode(401)
                    .body("detail", containsString("아이디 또는 비밀번호가 올바르지 않습니다"))
                    .cookie(AUTH_SESSION_COOKIE_NAME, nullValue());
        }
    }

    @Nested
    @DisplayName("로그인 로그")
    class LoginLog {

        @Test
        @DisplayName("로그인 시도시 로그인 로그를 기록한다")
        void loginAttemptsAreLogged() {
            // when: 실패한 로그인 시도
            UserApiTestClient.login(new UserApiRequest.Login(
                    registerRequest.loginId(),
                    "wrong_password"
            ));

            // then: 로그인 실패 로그 검증
            assertThat(true).isFalse();

            // when: 성공한 로그인 시도
            UserApiTestClient.login(new UserApiRequest.Login(
                    registerRequest.loginId(),
                    registerRequest.password()
            ));

            // then: 로그인 성공 로그 검증
            // then: 로그인 이력 조회 (관리자 권한 필요)
            // 실제 구현에서는 LoginLog 엔티티가 저장되어야 함
            // - IP 주소
            // - User-Agent
            // - 로그인 시각
            // - 성공/실패 여부
            // - 실패 사유 (잘못된 비밀번호, 존재하지 않는 아이디 등)
            assertThat(false);
        }
    }
}
