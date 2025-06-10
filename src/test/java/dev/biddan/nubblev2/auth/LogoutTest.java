package dev.biddan.nubblev2.auth;

import static org.assertj.core.api.Assertions.assertThat;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.auth.controller.AuthApiRequest;
import dev.biddan.nubblev2.auth.domain.AuthSession;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.user.UserApiTestClient;
import dev.biddan.nubblev2.user.UserRequestFixture;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("로그아웃 테스트")
class LogoutTest extends AbstractIntegrationTest {

    private UserApiRequest.Register registerRequest;
    private String sessionId;
    private Cookie sessionCookie;

    @BeforeEach
    void setUp() {
        // given: 회원가입 및 로그인된 사용자
        registerRequest = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(registerRequest);

        AuthApiRequest.Login loginRequest = new AuthApiRequest.Login(
                registerRequest.loginId(),
                registerRequest.password()
        );

        Response loginResponse = AuthApiTestClient.login(loginRequest);
        sessionCookie = loginResponse.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        sessionId = sessionCookie.getValue();
    }

    @Nested
    @DisplayName("정상 로그아웃")
    class SuccessfulLogout {

        @Test
        @DisplayName("유효한 세션으로 로그아웃하면 세션이 삭제되고 쿠키가 만료된다")
        void logoutWithValidSession() {
            // given: 로그인 후 세션이 존재함을 확인
            assertThat(authSessionRepository.findBySessionId(sessionId)).isPresent();

            // when: 로그아웃
            Response response = AuthApiTestClient.logout(sessionCookie);

            // then: 200 OK
            response.then().statusCode(200);

            // then: 세션이 DB에서 삭제됨
            assertThat(authSessionRepository.findBySessionId(sessionId)).isEmpty();

            // then: 만료된 쿠키 반환
            checkExpiresSession(response);
        }

        @Test
        @DisplayName("이미 만료된 세션으로 로그아웃해도 에러 없이 처리된다")
        void logoutWithExpiredSession() {
            // given: 세션을 만료 상태로 변경
            AuthSession session = authSessionRepository.findBySessionId(sessionId).orElseThrow();

            ReflectionTestUtils.setField(session, "expiresAt", LocalDateTime.now().minusHours(1));
            authSessionRepository.save(session);

            // when: 로그아웃
            Response response = AuthApiTestClient.logout(sessionCookie);
            ;

            // then: 200 OK (에러 없이 처리)
            response.then().statusCode(200);

            // then: 만료된 쿠키 반환
            checkExpiresSession(response);
        }
    }

    @Nested
    @DisplayName("쿠키가 없거나 유효하지 않은 경우")
    class InvalidCookieCases {

        @Test
        @DisplayName("쿠키 없이 로그아웃해도 에러 없이 처리된다")
        void logoutWithoutCookie() {
            // when: 쿠키 없이 로그아웃
            Response response = AuthApiTestClient.logout(sessionCookie);

            // then: 200 OK (에러 없이 처리)
            response.then().statusCode(200);

            // then: 만료된 쿠키 반환
            checkExpiresSession(response);
        }

        @Test
        @DisplayName("유효하지 않은 세션 ID로 로그아웃해도 에러 없이 처리된다")
        void logoutWithInvalidSessionId() {
            // given: 존재하지 않는 세션 ID
            String invalidSessionId = UUID.randomUUID().toString();
            Cookie invalidCookie = new Cookie.Builder(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME,
                    invalidSessionId)
                    .build();

            // when: 유효하지 않은 쿠키로 로그아웃
            Response response = AuthApiTestClient.logout(invalidCookie);

            // then: 200 OK (에러 없이 처리)
            response.then().statusCode(200);

            // then: 만료된 쿠키 반환
            checkExpiresSession(response);
        }

        @Test
        @DisplayName("빈 세션 ID로 로그아웃해도 에러 없이 처리된다")
        void logoutWithEmptySessionId() {
            // given: 빈 세션 ID 쿠키
            Cookie emptyCookie = new Cookie.Builder(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME, "")
                    .build();

            // when: 빈 쿠키로 로그아웃
            Response response = AuthApiTestClient.logout(emptyCookie);

            // then: 200 OK (에러 없이 처리)
            response.then().statusCode(200);
        }

    }

    @Nested
    @DisplayName("로그아웃 후 동작")
    class AfterLogout {

        @Test
        @DisplayName("로그아웃 후 동일한 세션으로 다시 로그아웃 시도해도 에러 없이 처리된다")
        void logoutTwiceWithSameSession() {
            // given: 첫 번째 로그아웃
            AuthApiTestClient.logout(sessionCookie);

            // when: 같은 쿠키로 두 번째 로그아웃
            Response response = AuthApiTestClient.logout(sessionCookie);

            // then: 200 OK (에러 없이 처리)
            response.then().statusCode(200);
        }

        @Test
        @DisplayName("한 세션을 로그아웃해도 같은 사용자의 다른 세션은 영향받지 않는다")
        void logoutDoesNotAffectOtherSessions() {
            // given: 같은 사용자로 두 번째 로그인 (다른 세션 생성)
            AuthApiRequest.Login loginRequest = new AuthApiRequest.Login(
                    registerRequest.loginId(),
                    registerRequest.password()
            );
            Response secondLoginResponse = AuthApiTestClient.login(loginRequest);
            String secondSessionId = secondLoginResponse.getCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);

            // when: 첫 번째 세션으로 로그아웃
            AuthApiTestClient.logout(sessionCookie);

            // then: 첫 번째 세션은 삭제됨
            assertThat(authSessionRepository.findBySessionId(sessionId)).isEmpty();

            // then: 두 번째 세션은 여전히 유효함
            assertThat(authSessionRepository.findBySessionId(secondSessionId)).isPresent();
        }

    }

    private static void checkExpiresSession(Response response) {
        Cookie expiredCookie = response.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        assertThat(expiredCookie.getMaxAge()).isZero();
    }
}
