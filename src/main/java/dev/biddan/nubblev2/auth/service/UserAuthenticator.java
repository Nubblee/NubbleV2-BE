package dev.biddan.nubblev2.auth.service;

import dev.biddan.nubblev2.auth.service.AuthCommand.Login;
import dev.biddan.nubblev2.exception.http.UnauthorizedException;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import dev.biddan.nubblev2.user.service.PasswordHasher;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticator {

    private final UserRepository userRepository;
    private final LoginLogRecorder loginLogRecorder;
    private final PasswordHasher passwordHasher;

    public UserInfo.Private authenticate(Login loginCommand) {
        User user = userRepository.findByLoginId(loginCommand.loginId())
                .orElseThrow(() -> {
                            loginLogRecorder.recordFailure(
                                    loginCommand.loginId(),
                                    loginCommand.clientIp(),
                                    loginCommand.userAgent(),
                                    "아이디를 찾을 수 없습니다"
                            );

                            return new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다");
                });

        if (!passwordHasher.matches(loginCommand.password(), user.getPassword())) {
            loginLogRecorder.recordFailure(
                    loginCommand.loginId(),
                    loginCommand.clientIp(),
                    loginCommand.userAgent(),
                    "비밀번호가 일치하지 않습니다"
            );

            throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        return UserInfo.Private.from(user);
    }
}
