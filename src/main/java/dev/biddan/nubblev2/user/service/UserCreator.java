package dev.biddan.nubblev2.user.service;

import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import dev.biddan.nubblev2.user.service.dto.UserCommand;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreator {

    private final UserRepository userRepository;

    @Transactional
    public User create(UserCommand.Register command) {
        User newUser = User.builder()
                .loginId(command.loginId())
                .nickname(command.nickname())
                .password(command.password())
                .preferredArea(command.preferredArea())
                .email(command.email())
                .build();

        return userRepository.save(newUser);
    }
}
