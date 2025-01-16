package dev.biddan.nubblev2.user.feature.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.error.exception.UserNicknameAlreadyExistsException;
import dev.biddan.nubblev2.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("유저 등록 기능")
@SpringBootTest
@Transactional
class UserRegisterServiceTest {

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저 등록이 정상적으로 완료된다")
    @Test
    void success() {
        // given
        UserRegisterService.UserRegisterCommand command = new UserRegisterService.UserRegisterCommand(
                "user123",
                "nickname123",
                "password123",
                "경기도 군포시",
                "user@email.com"
        );

        // when
        Long newUserId = userRegisterService.register(command);

        // then
        User newUser = userRepository.findById(newUserId)
                .orElseThrow();

        assertThat(newUser).isNotNull();
        assertThat(newUser.getLoginId()).isEqualTo(command.loginId());
        assertThat(newUser.getNickname()).isEqualTo(command.nickname());
        assertThat(newUser.getPassword()).isEqualTo(command.password());
        assertThat(newUser.getPreferredArea()).isEqualTo(command.preferredArea());
        assertThat(newUser.getEmail()).isEqualTo(command.email());
    }

    @DisplayName("닉네임이 중복인 경우 예외를 발생시킨다")
    @Test
    void throwException() {
        // given
        UserRegisterService.UserRegisterCommand existingCommand = new UserRegisterService.UserRegisterCommand(
                "user123",
                "nickname123",
                "password123",
                "경기도 군포시",
                "user1@email.com"
        );
        userRegisterService.register(existingCommand);

        UserRegisterService.UserRegisterCommand duplicateCommand = new UserRegisterService.UserRegisterCommand(
                "user456",
                "nickname123",       // 중복된 nickname
                "password456",
                "서울시 강남구",
                "user2@email.com"
        );

        // when & then
        assertThatThrownBy(() -> userRegisterService.register(duplicateCommand))
                .isInstanceOf(UserNicknameAlreadyExistsException.class);
    }
}
