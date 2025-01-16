package dev.biddan.nubblev2.user.feature.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.biddan.nubblev2.common.error.ApiErrorResponse;
import dev.biddan.nubblev2.user.error.UserErrorCode;
import dev.biddan.nubblev2.user.error.exception.UserLoginIdAlreadyExistsException;
import dev.biddan.nubblev2.user.error.exception.UserNicknameAlreadyExistsException;
import dev.biddan.nubblev2.user.feature.register.UserRegisterApiController.UserRegisterResponse;
import dev.biddan.nubblev2.user.feature.register.UserRegisterService.UserRegisterCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@DisplayName("유저 등록 엔드포인트")
@WebMvcTest(UserRegisterApiController.class)
class UserRegisterApiControllerTest {

    @Autowired
    private MockMvcTester tester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRegisterService userRegisterService;

    @DisplayName("유저 등록 성공")
    @Test
    void success() throws JsonProcessingException {
        // given
        UserRegisterService.UserRegisterCommand command = UserRegisterCommand.builder()
                .loginId("user123")
                .nickname("user123")
                .password("password123")
                .preferredArea("경기도 군포시")
                .email("user@email.com")
                .build();

        long newUserId = 1L;
        given(userRegisterService.register(command))
                .willReturn(newUserId);

        UserRegisterResponse expected = new UserRegisterResponse(newUserId);

        // when
        MvcTestResult result = tester.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command))
                .exchange();

        // then
        result.assertThat()
                .hasStatus(HttpStatus.CREATED)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().convertTo(UserRegisterResponse.class)
                .isEqualTo(expected);
    }

    @DisplayName("유저 등록 실패 - 아이디 중복")
    @Test
    void failure_UserLoginIdAlreadyExists() throws JsonProcessingException {
        // given
        UserRegisterCommand command = UserRegisterCommand.builder()
                .loginId("existingUser")
                .nickname("user123")
                .password("password123")
                .preferredArea("경기도 군포시")
                .email("user@email.com")
                .build();

        given(userRegisterService.register(command))
                .willThrow(new UserLoginIdAlreadyExistsException("existingUser"));

        // when
        MvcTestResult result = tester.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command))
                .exchange();

        // then
        result.assertThat()
                .hasStatus(HttpStatus.CONFLICT)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().convertTo(ApiErrorResponse.class)
                .satisfies(response -> {
                    assertThat(response.errorCode()).isEqualTo(UserErrorCode.LOGIN_ID_ALREADY_EXISTS.name());
                    assertThat(response.errorMessage()).isNotBlank();
                });
    }

    @DisplayName("유저 등록 실패 - 닉네임 중복")
    @Test
    void failure_UserNicknameAlreadyExists() throws JsonProcessingException {
        // given
        UserRegisterCommand command = UserRegisterCommand.builder()
                .loginId("user123")
                .nickname("existingNickname")
                .password("password123")
                .preferredArea("경기도 군포시")
                .email("user@email.com")
                .build();

        given(userRegisterService.register(command))
                .willThrow(new UserNicknameAlreadyExistsException("existingUser"));

        // when
        MvcTestResult result = tester.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command))
                .exchange();

        // then
        result.assertThat()
                .hasStatus(HttpStatus.CONFLICT)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().convertTo(ApiErrorResponse.class)
                .satisfies(response -> {
                    assertThat(response.errorCode()).isEqualTo(UserErrorCode.NICKNAME_ALREADY_EXISTS.name());
                    assertThat(response.errorMessage()).isNotBlank();
                });
    }
}
