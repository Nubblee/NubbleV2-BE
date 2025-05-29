package dev.biddan.nubblev2.auth.service;

import dev.biddan.nubblev2.auth.repository.AuthSessionRepository;
import dev.biddan.nubblev2.auth.service.AuthCommand.Login;
import dev.biddan.nubblev2.auth.service.AuthSessionInfo.WithUser;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthenticator userAuthenticator;
    private final AuthSessionCreator authSessionCreator;
    private final AuthSessionValidator authSessionValidator;
    private final AuthSessionRepository authSessionRepository;

    public WithUser login(Login loginCommand) {
        UserInfo.Private privateUserInfo = userAuthenticator.authenticate(loginCommand);

        AuthSessionInfo.Basic authSessionInfo = authSessionCreator.create(
                privateUserInfo,
                loginCommand.clientIp(),
                loginCommand.userAgent());

        return new WithUser(privateUserInfo, authSessionInfo);
    }

    @Transactional
    public void logout(String sessionId) {
        authSessionValidator.findValidSession(sessionId)
                .ifPresent(authSessionRepository::delete);
    }
}
