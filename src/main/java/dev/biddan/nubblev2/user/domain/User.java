package dev.biddan.nubblev2.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    private static final int MAX_NICKNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_BIRTH_YEAR = 1900;
    private static final int MAX_SEX_LENGTH = 5;
    private static final int MAX_ADDRESS_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_NICKNAME_LENGTH, unique = true)
    private String nickname;

    @Column(nullable = false, length = MAX_PASSWORD_LENGTH)
    private String password;

    @Column(nullable = false)
    private int birthYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = MAX_SEX_LENGTH)
    private Sex sex;

    @Column(nullable = false, length = MAX_ADDRESS_LENGTH)
    private String address;

    @Builder
    public User(String nickname, String password, int birthYear, Sex sex, String address) {
        Assert.hasText(nickname, "닉네임은 공백일 수 없습니다.");
        Assert.isTrue(nickname.length() <= MAX_NICKNAME_LENGTH,
                String.format("닉네임은 %d자까지 입력 가능합니다.", MAX_NICKNAME_LENGTH));

        Assert.hasText(password, "비밀번호는 공백일 수 없습니다.");
        Assert.isTrue(MIN_PASSWORD_LENGTH <= password.length() && password.length() <= MAX_PASSWORD_LENGTH,
                String.format("비밀번호는 %d자 이상 %d자 이하여야 합니다.", MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));

        int currentYear = LocalDate.now().getYear();
        Assert.isTrue(birthYear >= MIN_BIRTH_YEAR && birthYear <= currentYear,
                String.format("출생연도는 %d년부터 %d년 사이여야 합니다.", MIN_BIRTH_YEAR, currentYear));

        Assert.notNull(sex, "성별을 입력해주세요.");

        Assert.hasText(address, "주소를 입력해주세요.");
        Assert.isTrue(address.length() <= MAX_ADDRESS_LENGTH,
                String.format("주소는 %d자까지 입력 가능합니다.", MAX_ADDRESS_LENGTH));

        this.nickname = nickname;
        this.password = password;
        this.birthYear = birthYear;
        this.sex = sex;
        this.address = address;
    }
}
