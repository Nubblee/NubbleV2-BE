package dev.biddan.nubblev2.argument.userid;

import dev.biddan.nubblev2.auth.domain.AuthSession;
import dev.biddan.nubblev2.auth.service.AuthSessionValidator;
import dev.biddan.nubblev2.interceptor.auth.CookieExtractor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthSessionValidator authSessionValidator;
    private final CookieExtractor cookieExtractor;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class) &&
                (Long.class.isAssignableFrom(parameter.getParameterType()) ||
                        Optional.class.isAssignableFrom(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Object userIdFromAttribute = webRequest.getAttribute("currentUserId", RequestAttributes.SCOPE_REQUEST);

        if (userIdFromAttribute instanceof Long userId) {
            return handleReturnType(parameter, userId);
        }

        Long userId = extractUserIdDirectly(webRequest);

        return handleReturnType(parameter, userId);
    }

    private Long extractUserIdDirectly(NativeWebRequest webRequest) {
        try {
            HttpServletRequest httpRequest = webRequest.getNativeRequest(HttpServletRequest.class);

            if (httpRequest == null) {
                return null;
            }

            String sessionId = cookieExtractor.extractSessionId(httpRequest).orElse(null);
            if (sessionId == null) {
                return null;
            }

            return authSessionValidator.findValidSession(sessionId)
                    .map(AuthSession::getUserId)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private Object handleReturnType(MethodParameter parameter, Long userId) {
        if (Optional.class.isAssignableFrom(parameter.getParameterType())) {
            return Optional.ofNullable(userId);
        }

        return userId;
    }
}
