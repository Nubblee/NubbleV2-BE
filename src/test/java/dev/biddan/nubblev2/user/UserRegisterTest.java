package dev.biddan.nubblev2.user;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import dev.biddan.nubblev2.user.controller.UserApiRequest;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("회원가입 테스트")
class UserRegisterTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private UserApiTestClient userApiTestClient;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userApiTestClient = new UserApiTestClient();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("정상 회원가입 후 중복 가입시 실패")
    void registerCompleteFlow() {
        // given: 회원가입할 정보 정의
        UserApiRequest.Register request = generateValidUserRegisterRequest();

        // when & then: 1. 정상 회원가입
        Response successResponse = userApiTestClient.register(request);

        successResponse.then()
                .statusCode(201)
                .body("user.id", notNullValue())
                .body("user.loginId", equalTo(request.loginId()))
                .body("user.nickname", equalTo(request.nickname()))
                .body("user.password", nullValue()); // 비밀번호 노출 안됨

        // then: DB에 저장 확인
        Long userId = successResponse.jsonPath().getLong("user.id");

        assertThat(userRepository.count()).isEqualTo(1);
        User savedUser = userRepository.findById(userId).orElseThrow();
        // 비밀번호 해싱 확인
        assertThat(savedUser.getPassword()).isNotEqualTo(request.password());

        // when & then: 2. 중복 가입 실패 (가장 중요한 중복 검증만)
        userApiTestClient.register(request)
                .then()
                .statusCode(409)
                .body("detail", anyOf(
                        containsString("이미 사용중인 아이디"),
                        containsString("이미 사용 중인 닉네임")
                ));

        // then: DB에는 여전히 1명만 존재
        assertThat(userRepository.count()).isEqualTo(1);
    }

    private UserApiRequest.Register generateValidUserRegisterRequest() {
        // 테스트 격리를 위해 랜덤값 사용
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);

        return UserApiRequest.Register.builder()
                .loginId("testuser_" + randomSuffix)
                .nickname("테스트닉네임_" + randomSuffix)
                .password("password123!")
                .preferredArea("서울시 강남구")
                .email("test_" + randomSuffix + "@example.com")
                .build();
    }

    static class UserApiTestClient {

        public Response register(UserApiRequest.Register request) {
            return given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/users")
                    .then()
                    .log().ifError()
                    .extract().response();
        }
    }
}
