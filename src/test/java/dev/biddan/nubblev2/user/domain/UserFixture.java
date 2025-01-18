package dev.biddan.nubblev2.user.domain;

public class UserFixture {

    public static final String DEFAULT_LOGIN_ID = "dbdnjsdn123";
    public static final String DEFAULT_NICKNAME = "침착맨";
    public static final String DEFAULT_PASSWORD = "eBWH6Y7A3r7rqs9kf";
    public static final String DEFAULT_PREFERRED_AREA = "서울시 강남구";
    public static final String DEFAULT_EMAIL = null;

    private final User.UserBuilder builder;

    public static UserFixture aUser() {
        return new UserFixture();
    }

    private UserFixture() {
        builder = User.builder()
                .loginId(DEFAULT_LOGIN_ID)
                .nickname(DEFAULT_NICKNAME)
                .password(DEFAULT_PASSWORD)
                .preferredArea(DEFAULT_PREFERRED_AREA)
                .email(DEFAULT_EMAIL);
    }

    public UserFixture withLoginId(final String loginId) {
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
