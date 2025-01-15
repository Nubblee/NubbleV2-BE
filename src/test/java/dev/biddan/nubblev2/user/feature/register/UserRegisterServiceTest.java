package dev.biddan.nubblev2.user.feature.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.biddan.nubblev2.user.domain.Sex;
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
                "hellohellohellohello",
                "password",
                1900,
                "none",
                "경기도 군포시"
        );

        // when
        Long newUserId = userRegisterService.register(command);

        // then
        User newUser = userRepository.findById(newUserId)
                .orElseThrow();

        assertThat(newUser).isNotNull();
        assertThat(newUser.getNickname()).isEqualTo(command.nickname());
        assertThat(newUser.getPassword()).isEqualTo(command.password());
        assertThat(newUser.getBirthYear()).isEqualTo(command.birthYear());
        assertThat(newUser.getSex()).isEqualTo(Sex.NONE);
        assertThat(newUser.getAddress()).isEqualTo(command.address());
    }

    @DisplayName("닉네임이 중복인 경우 예외를 발생시킨다")
    @Test
    void throwException() {
        // given
        UserRegisterService.UserRegisterCommand existingCommand = new UserRegisterService.UserRegisterCommand(
                "dup",
                "password",
                2025,
                "MALE",
                "경기도 군포시"
        );
        // 중복 닉네임을 가진 유저 생성
        userRegisterService.register(existingCommand);

        UserRegisterService.UserRegisterCommand duplicateNicknameCommand = new UserRegisterService.UserRegisterCommand(
                "dup",
                "password123",
                1995,
                "FEMALE",
                "서울시 서초구"
        );

        // when & then
        assertThatThrownBy(() -> userRegisterService.register(duplicateNicknameCommand))
                .isInstanceOf(UserNicknameAlreadyExistsException.class);
    }
}
