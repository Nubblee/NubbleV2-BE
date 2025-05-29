package dev.biddan.nubblev2.auth.service;

import dev.biddan.nubblev2.auth.domain.LoginLog;
import dev.biddan.nubblev2.auth.repository.LoginLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LoginLogRecorder {

    private final LoginLogRepository loginLogRepository;

    @Transactional
    public void recordFailure(String loginId, String clientIp, String userAgent, String failureReason) {
        LoginLog failureLog = LoginLog.failure(loginId, clientIp, userAgent, failureReason);

        loginLogRepository.save(failureLog);
    }

    @Transactional
    public void recordSuccess(String loginId, String clientIp, String userAgent, String sessionId) {
        LoginLog successLog = LoginLog.success(loginId, clientIp, userAgent, sessionId);
        loginLogRepository.save(successLog);
    }

}
