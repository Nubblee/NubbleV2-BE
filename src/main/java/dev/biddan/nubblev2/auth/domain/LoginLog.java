package dev.biddan.nubblev2.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "login_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LoginLog {

    private static final int LOGIN_ID_MAX_LENGTH = 20;
    private static final int IP_ADDRESS_MAX_LENGTH = 45;
    private static final int USER_AGENT_MAX_LENGTH = 255;
    private static final int FAILURE_REASON_MAX_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", length = LOGIN_ID_MAX_LENGTH, nullable = false)
    private String loginId;

    @Column(name = "ip_address", length = IP_ADDRESS_MAX_LENGTH)
    private String ipAddress;

    @Column(name = "user_agent", length = USER_AGENT_MAX_LENGTH)
    private String userAgent;

    @Column(name = "login_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginStatus loginStatus;

    @Column(name = "failure_reason", length = FAILURE_REASON_MAX_LENGTH)
    private String failureReason;

    @Column(name = "attempted_at", nullable = false)
    private LocalDateTime attemptedAt;

    private String authSessionId;

    public enum LoginStatus {
        SUCCESS,
        FAILURE
    }

    @Builder
    private LoginLog(String loginId, String ipAddress, String userAgent,
            LoginStatus loginStatus, String failureReason, String authSessionId) {
        this.loginId = loginId;
        this.loginStatus = loginStatus;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.failureReason = failureReason;
        this.attemptedAt = LocalDateTime.now();
        this.authSessionId = authSessionId;
    }

    public static LoginLog success(String loginId, String ipAddress, String userAgent, String authSessionId) {
        return LoginLog.builder()
                .loginId(loginId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .loginStatus(LoginStatus.SUCCESS)
                .authSessionId(authSessionId)
                .build();
    }

    public static LoginLog failure(String loginId, String ipAddress, String userAgent, String failureReason) {
        return LoginLog.builder()
                .loginId(loginId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .loginStatus(LoginStatus.FAILURE)
                .failureReason(failureReason)
                .build();
    }
}
