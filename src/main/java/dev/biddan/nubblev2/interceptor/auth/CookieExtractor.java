package dev.biddan.nubblev2.interceptor.auth;

import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CookieExtractor {

    public Optional<String> extractSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME.equals(cookie.getName())) {
                String value = cookie.getValue();
                return (value != null && !value.trim().isEmpty())
                        ? Optional.of(value.trim())
                        : Optional.empty();
            }
        }
        return Optional.empty();
    }
}
