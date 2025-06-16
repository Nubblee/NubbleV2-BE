package dev.biddan.nubblev2.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {

    private static final int MIN_LOGIN_ID_LENGTH = 4;
    private static final int MAX_LOGIN_ID_LENGTH = 20;
    private static final int MAX_NICKNAME_LENGTH = 20;
    private static final int MAX_PASSWORD_LENGTH = 72;
    private static final int MAX_PREFERRED_AREA_LENGTH = 30;
    private static final int MAX_EMAIL_LENGTH = 254; // RFC 3696 기준
    private static final int MAX_PROFILE_IMAGE_URL_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_LOGIN_ID_LENGTH, unique = true)
    private String loginId;

    @Column(nullable = false, length = MAX_NICKNAME_LENGTH, unique = true)
    private String nickname;

    @Column(nullable = false, length = MAX_PASSWORD_LENGTH)
    private String password;

    @Column(nullable = false, length = MAX_PREFERRED_AREA_LENGTH)
    private String preferredArea;

    @Column(length = MAX_EMAIL_LENGTH)
    private String email;

    @Column(name = "profile_image_url", length = MAX_PROFILE_IMAGE_URL_LENGTH)
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public User(String loginId, String nickname, String password, String preferredArea, String email, String profileImageUrl) {
        Assert.hasText(loginId, "로그인 ID는 공백일 수 없습니다.");
        Assert.isTrue(MIN_LOGIN_ID_LENGTH <= loginId.length() && loginId.length() <= MAX_LOGIN_ID_LENGTH,
                String.format("로그인 ID는 %d자 이상 %d자 이하여야 합니다.", MIN_LOGIN_ID_LENGTH, MAX_LOGIN_ID_LENGTH));

        Assert.hasText(nickname, "닉네임은 공백일 수 없습니다.");
        Assert.isTrue(nickname.length() <= MAX_NICKNAME_LENGTH,
                String.format("닉네임은 %d자까지 입력 가능합니다.", MAX_NICKNAME_LENGTH));

        Assert.hasText(password, "비밀번호는 공백일 수 없습니다.");
        Assert.isTrue(password.length() <= MAX_PASSWORD_LENGTH, String.format("비밀번호는 %d자 이하여야 합니다.", MAX_PASSWORD_LENGTH));

        Assert.hasText(preferredArea, "선호 지역을 입력해주세요.");
        Assert.isTrue(preferredArea.length() <= MAX_PREFERRED_AREA_LENGTH,
                String.format("선호 지역은 %d자까지 입력 가능합니다.", MAX_PREFERRED_AREA_LENGTH));

        if (email != null) {
            Assert.isTrue(email.length() <= MAX_EMAIL_LENGTH,
                    String.format("이메일은 %d자까지 입력 가능합니디ㅏ.", MAX_EMAIL_LENGTH));
        }

        if (profileImageUrl != null) {
            Assert.isTrue(profileImageUrl.length() <= MAX_PROFILE_IMAGE_URL_LENGTH,
                    String.format("프로필 이미지 URL은 %d자까지 입력 가능합니다.", MAX_PROFILE_IMAGE_URL_LENGTH));
        }

        this.loginId = loginId;
        this.nickname = nickname;
        this.password = password;
        this.preferredArea = preferredArea;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
