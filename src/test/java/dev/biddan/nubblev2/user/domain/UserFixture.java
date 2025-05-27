package dev.biddan.nubblev2.user.domain;

import java.util.concurrent.atomic.AtomicLong;

public class UserFixture {

    private static final AtomicLong ID_COUNTER = new AtomicLong(1);

    private static final String DEFAULT_PASSWORD = "password123";
    private static final String DEFAULT_PREFERRED_AREA = "서울시 강남구";

    private final User.UserBuilder builder;

    public static UserFixture aUser() {
        return new UserFixture();
    }

    private UserFixture() {
        long uniqueId = ID_COUNTER.getAndIncrement();
        this.builder = User.builder()
                .loginId("user" + uniqueId)
                .nickname("닉네임" + uniqueId)
                .password(DEFAULT_PASSWORD)
                .preferredArea(DEFAULT_PREFERRED_AREA);
    }

    public UserFixture withLoginId(String loginId) {
        builder.loginId(loginId);
        return this;
    }

    public UserFixture withNickname(String nickname) {
        builder.nickname(nickname);
        return this;
    }

    public UserFixture withPassword(String password) {
        builder.password(password);
        return this;
    }

    public UserFixture withPreferredArea(String preferredArea) {
        builder.preferredArea(preferredArea);
        return this;
    }

    public UserFixture withEmail(String email) {
        builder.email(email);
        return this;
    }

    public User build() {
        return builder.build();
    }
}
