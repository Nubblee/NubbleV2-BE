package dev.biddan.nubblev2.auth.service;

import dev.biddan.nubblev2.auth.domain.AuthSession;
import dev.biddan.nubblev2.auth.repository.AuthSessionRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthSessionValidator {

    private final AuthSessionRepository authSessionRepository;

    @Transactional(readOnly = true)
    public Optional<AuthSession> findValidSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return Optional.empty();
        }

        return authSessionRepository.findBySessionId(sessionId)
                .filter(session -> !session.isExpired());
    }
}
