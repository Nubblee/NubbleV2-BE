package dev.biddan.nubblev2.http;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthSessionCookieManager {

    public static final String AUTH_SESSION_COOKIE_NAME = "auth-session-id";

    public ResponseCookie createSessionCookie(String sessionId) {
        return ResponseCookie.from(AUTH_SESSION_COOKIE_NAME, sessionId)
                .httpOnly(true)
                .path("/")
                .sameSite(SameSite.LAX.toString())
                .secure(false)
                .build();
    }
}
