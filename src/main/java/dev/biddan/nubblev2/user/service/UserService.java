package dev.biddan.nubblev2.user.service;

import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.service.dto.UserCommand;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDuplicateValidator userDuplicateValidator;
    private final UserCreator userCreator;
    private final PasswordHasher passwordHasher;

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
}
