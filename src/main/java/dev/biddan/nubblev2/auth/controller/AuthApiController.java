package dev.biddan.nubblev2.auth.controller;

import dev.biddan.nubblev2.auth.service.AuthService;
import dev.biddan.nubblev2.auth.service.AuthSessionInfo.WithUser;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.http.HttpIpExtractor;
import dev.biddan.nubblev2.user.controller.dto.UserApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/auth")
public class AuthApiController {

    private final HttpIpExtractor httpIpExtractor;
    private final AuthService authService;
    private final AuthSessionCookieManager authSessionCookieManager;

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApiResponse.Private> login(
            @RequestBody @Valid AuthApiRequest.Login request,
            HttpServletRequest httpServletRequest
    ) {

        String ipAddress = httpIpExtractor.getClientIpAddress(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");

        WithUser loginResult = authService.login(request.toLoginCommand(ipAddress, userAgent));

        ResponseCookie sessionCookie = authSessionCookieManager.createSessionCookie(
                loginResult.authSession().sessionId());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(new UserApiResponse.Private(loginResult.userInfo()));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = AuthSessionCookieManager.AUTH_SESSION_COOKIE_NAME, required = false) String sessionId
    ) {
        if (sessionId != null) {
            authService.logout(sessionId);
        }

        ResponseCookie expiredCookie = authSessionCookieManager.createExpiredSessionCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }
}
