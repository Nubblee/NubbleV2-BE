package dev.biddan.nubblev2.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.biddan.nubblev2.AbstractIntegrationTest;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import dev.biddan.nubblev2.user.domain.User;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원가입 테스트")
class UserRegisterTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("회원가입 후 중복 가입시 409를 반환한다")
    void registerCompleteFlow() {
        // given: 회원가입할 정보 정의
        UserApiRequest.Register request = UserRequestFixture.generateValidUserRegisterRequest();

        // when & then: 1. 정상 회원가입
        Response successResponse = UserApiTestClient.register(request);

        successResponse.then()
                .statusCode(201)
                .body("user.id", notNullValue())
                .body("user.loginId", equalTo(request.loginId()))
                .body("user.nickname", equalTo(request.nickname()))
                .body("user.password", nullValue())
                .body("user.createdAt", notNullValue()); // 비밀번호 노출 안됨

        // then: DB에 저장 확인
        Long userId = successResponse.jsonPath().getLong("user.id");

        assertThat(userRepository.count()).isEqualTo(1);
        User savedUser = userRepository.findById(userId).orElseThrow();
        // 비밀번호 해싱 확인
        assertThat(savedUser.getPassword()).isNotEqualTo(request.password());

        // when & then: 2. 중복 가입 실패 (가장 중요한 중복 검증만)
        UserApiTestClient.register(request)
                .then()
                .statusCode(409)
                .body("detail", anyOf(
                        containsString("이미 사용중인 아이디"),
                        containsString("이미 사용 중인 닉네임")
                ));

        // then: DB에는 여전히 1명만 존재
        assertThat(userRepository.count()).isEqualTo(1);
    }
}
