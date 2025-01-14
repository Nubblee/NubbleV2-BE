package dev.biddan.nubblev2.user.domain;

public class UserFixture {

    public static final String DEFAULT_NICKNAME = "Branon Virgen";
    public static final String DEFAULT_PASSWORD = "eBWH6Y7A3r7rqs9kf";
    public static final int DEFAULT_BIRTH_YEAR = 2013;
    public static final Sex DEFAULT_SEX = Sex.NONE;
    public static final String DEFAULT_ADDRESS = "서울시 강남구";

    private final User.UserBuilder builder;

    public static UserFixture aUser() {
        return new UserFixture();
    }

    private UserFixture() {
        builder = User.builder()
                .nickname(DEFAULT_NICKNAME)
                .password(DEFAULT_PASSWORD)
                .birthYear(DEFAULT_BIRTH_YEAR)
                .sex(DEFAULT_SEX)
                .address(DEFAULT_ADDRESS);
    }

    public UserFixture withNickname(String nickname) {
        builder.nickname(nickname);
        return this;
    }

    public UserFixture withPassword(String password) {
        builder.password(password);
        return this;
    }

    public UserFixture withBirthYear(int birthYear) {
        builder.birthYear(birthYear);
        return this;
    }

    public UserFixture withAddress(String address) {
        builder.address(address);
        return this;
    }

    public UserFixture withSex(Sex sex) {
        builder.sex(sex);
        return this;
    }

    public User build() {
        return builder.build();
    }
}
