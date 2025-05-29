package dev.biddan.nubblev2.auth.service;

import dev.biddan.nubblev2.auth.domain.AuthSession;
import dev.biddan.nubblev2.auth.repository.AuthSessionRepository;
import dev.biddan.nubblev2.user.service.dto.UserInfo.Private;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthSessionCreator {

    private static final int SESSION_EXPIRATION_HOURS = 24;

    private final AuthSessionRepository authSessionRepository;
    private final LoginLogRecorder loginLogRecorder;

    @Transactional
    public AuthSessionInfo.Basic create(Private privateUserInfo, String clientIp, String userAgent) {
        AuthSession newSession = new AuthSession(
                privateUserInfo.id(),
                SESSION_EXPIRATION_HOURS
        );

        AuthSession savedSession = authSessionRepository.save(newSession);
        loginLogRecorder.recordSuccess(
                privateUserInfo.loginId(),
                clientIp,
                userAgent,
                String.valueOf(savedSession.getId())
        );

        return AuthSessionInfo.Basic.from(savedSession);
    }
}
