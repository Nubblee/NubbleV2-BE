package dev.biddan.nubblev2.user.service;

import dev.biddan.nubblev2.exception.http.NotFoundException;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import dev.biddan.nubblev2.user.service.dto.UserCommand;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDuplicateValidator userDuplicateValidator;
    private final UserCreator userCreator;
    private final PasswordHasher passwordHasher;
    private final UserRepository userRepository;

    public UserInfo.Private register(UserCommand.Register command) {
        userDuplicateValidator.validate(command.loginId(), command.nickname());

        String hashedPassword = passwordHasher.hash(command.password());
        UserCommand.Register hashedPasswordCommand = UserCommand.Register.builder()
                .loginId(command.loginId())
                .nickname(command.nickname())
                .password(hashedPassword)
                .preferredArea(command.preferredArea())
                .email(command.email())
                .build();

        User user = userCreator.create(hashedPasswordCommand);

        return UserInfo.Private.from(user);
    }

    @Transactional(readOnly = true)
    public UserInfo.Private getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다"));

        return UserInfo.Private.from(user);
    }
}
