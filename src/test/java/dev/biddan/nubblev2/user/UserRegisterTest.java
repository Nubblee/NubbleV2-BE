package dev.biddan.nubblev2.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import dev.biddan.nubblev2.user.domain.User;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.Cookie.SameSite;

@DisplayName("회원가입 테스트")
class UserRegisterTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("회원가입시 유저가 생성된다")
    void user_save() {
        // given: 회원가입할 정보 정의
        UserApiRequest.Register request = UserRequestFixture.generateValidUserRegisterRequest();

        // when: 회원가입
        Response response = UserApiTestClient.register(request);

        // then: 201 Created 응답과 사용자 정보 반환
        response.then()
                .statusCode(201)
                .body("user.id", notNullValue())
                .body("user.loginId", equalTo(request.loginId()))
                .body("user.nickname", equalTo(request.nickname()))
                .body("user.password", nullValue()) // 비밀번호 노출 안됨
                .body("user.createdAt", notNullValue());

        // then: DB에 저장 확인
        Long userId = response.jsonPath().getLong("user.id");

        User savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo(request.password());
        assertThat(savedUser.getLoginId()).isEqualTo(request.loginId());
        assertThat(savedUser.getNickname()).isEqualTo(request.nickname());
    }

    @Test
    @DisplayName("회원가입시 인증 쿠키도 함께 발급된다")
    void auth_login() {
        // given: 회원가입할 정보 정의
        UserApiRequest.Register request = UserRequestFixture.generateValidUserRegisterRequest();

        // when: 회원가입
        Response response = UserApiTestClient.register(request);

        // then: 인증 세션 쿠키 검증
        Cookie sessionCookie = response.getDetailedCookie(AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME);
        assertThat(sessionCookie).isNotNull();
        assertThat(sessionCookie.getValue()).isNotBlank();
        assertThat(sessionCookie.isHttpOnly()).isTrue();
        assertThat(sessionCookie.getPath()).isEqualTo("/");
        assertThat(sessionCookie.getSameSite()).isEqualTo(SameSite.LAX.toString());

        // then: 세션이 DB에 저장되었는지 확인
        String sessionId = sessionCookie.getValue();
        assertThat(authSessionRepository.findBySessionId(sessionId)).isPresent();

        // then: 로그인 성공 로그 기록 확인
        assertThat(loginLogRepository.count()).isEqualTo(1);
        assertThat(loginLogRepository.findAll().get(0).getLoginStatus())
                .isEqualTo(dev.biddan.nubblev2.auth.domain.LoginLog.LoginStatus.SUCCESS);
    }

    @Test
    @DisplayName("중복 로그인ID 가입시에 회원가입 실패")
    void fail_when_duplicate_loginId() {
        // given: 기존 사용자 회원가입
        UserApiRequest.Register existingUser = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(existingUser);

        // given: 같은 로그인ID, 다른 닉네임으로 가입 시도
        UserApiRequest.Register duplicateLoginIdUser = UserApiRequest.Register.builder()
                .loginId(existingUser.loginId()) // 같은 로그인ID
                .nickname("다른닉네임123")
                .password("password123!")
                .preferredArea("서울시 강남구")
                .email("different@example.com")
                .build();

        // when & then: 중복 로그인ID로 가입 시도
        UserApiTestClient.register(duplicateLoginIdUser)
                .then()
                .statusCode(409)
                .body("detail", containsString("이미 사용중인 아이디입니다"));
    }

    @Test
    @DisplayName("중복 닉네임 가입시에 회원가입 실패")
    void fail_when_duplicate_nickname() {
        // given: 기존 사용자 회원가입
        UserApiRequest.Register existingUser = UserRequestFixture.generateValidUserRegisterRequest();
        UserApiTestClient.register(existingUser);

        // given: 다른 로그인ID, 같은 닉네임으로 가입 시도
        UserApiRequest.Register duplicateNicknameUser = UserApiRequest.Register.builder()
                .loginId("differentuser123")
                .nickname(existingUser.nickname()) // 같은 닉네임
                .password("password123!")
                .preferredArea("서울시 강남구")
                .email("different@example.com")
                .build();

        // when & then: 중복 닉네임으로 가입 시도
        UserApiTestClient.register(duplicateNicknameUser)
                .then()
                .statusCode(409)
                .body("detail", containsString("이미 사용 중인 닉네임입니다"));
    }
}
