package dev.biddan.nubblev2.auth.repository;

import dev.biddan.nubblev2.auth.domain.AuthSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findBySessionId(String sessionId);
}
