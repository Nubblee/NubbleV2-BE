package dev.biddan.nubblev2.interceptor.auth;

import dev.biddan.nubblev2.auth.domain.AuthSession;
import dev.biddan.nubblev2.auth.service.AuthSessionValidator;
import dev.biddan.nubblev2.exception.http.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthSessionCheckInterceptor implements HandlerInterceptor {

    private final AuthSessionValidator authSessionValidator;
    private final CookieExtractor cookieExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!requiresAuthentication(handler)) {
            return true;
        }

        try {
            String authSessionId = cookieExtractor.extractSessionId(request).orElse(null);
            if (authSessionId == null) {
                throw new UnauthorizedException("인증이 필요합니다");
            }

            AuthSession authSession = authSessionValidator.findValidSession(authSessionId)
                            .orElseThrow(() -> new UnauthorizedException("유효하지 않은 세션ID입니다"));

            request.setAttribute("currentUserId", authSession.getUserId());
            log.debug("인증 성공: userId={}, uri={}", authSession.getUserId(), request.getRequestURI());
            return true;
        } catch (Exception e) {
            log.debug("인증 실패: uri={}, error={}", request.getRequestURI(), e.getMessage());
            throw e;
        }
    }

    private boolean requiresAuthentication(Object handler) {
        return handler instanceof HandlerMethod handlerMethod
                && handlerMethod.hasMethodAnnotation(AuthRequired.class);
    }
}
