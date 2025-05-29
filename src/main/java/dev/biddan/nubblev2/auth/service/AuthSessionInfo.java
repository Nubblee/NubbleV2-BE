package dev.biddan.nubblev2.auth.service;

import dev.biddan.nubblev2.auth.domain.AuthSession;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import java.time.LocalDateTime;
import lombok.Builder;

public class AuthSessionInfo {

    @Builder
    public record Basic(
            Long userId,
            String sessionId,
            LocalDateTime expiresAt
    ) {

        public static Basic from(AuthSession authSession) {
            return Basic.builder()
                    .userId(authSession.getUserId())
                    .sessionId(authSession.getSessionId())
                    .expiresAt(authSession.getExpiresAt())
                    .build();
        }
    }

    public record WithUser(
            UserInfo.Private userInfo,
            AuthSessionInfo.Basic authSession
    ) {
    }
}
