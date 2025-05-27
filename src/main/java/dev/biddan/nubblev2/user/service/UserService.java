package dev.biddan.nubblev2.user.service;

import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.error.exception.UserLoginIdAlreadyExistsException;
import dev.biddan.nubblev2.user.error.exception.UserNicknameAlreadyExistsException;
import dev.biddan.nubblev2.user.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long register(UserRegisterCommand command) {
        if (userRepository.existsByLoginId(command.loginId)) {
            throw new UserLoginIdAlreadyExistsException(command.loginId);
        }

        if (userRepository.existsByNickname(command.nickname)) {
            throw new UserNicknameAlreadyExistsException(command.nickname);
        }

        User newUser = User.builder()
                .loginId(command.loginId)
                .nickname(command.nickname)
                .password(command.password)
                .preferredArea(command.preferredArea)
                .email(command.email)
                .build();

        return userRepository.save(newUser)
                .getId();
    }

    @Builder
    public record UserRegisterCommand(
            String loginId,
            String nickname,
            String password,
            String preferredArea,
            String email
    ) {

    }
}
