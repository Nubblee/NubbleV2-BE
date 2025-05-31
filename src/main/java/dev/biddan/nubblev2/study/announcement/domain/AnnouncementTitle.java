package dev.biddan.nubblev2.study.announcement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AnnouncementTitle {

    private static final int MAX_LENGTH = 100;

    @Column(name = "title", nullable = false, length = MAX_LENGTH)
    private String value;

    public AnnouncementTitle(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        Assert.hasText(value, "공고 제목은 필수입니다");
        Assert.isTrue(value.length() <= MAX_LENGTH,
                String.format("공고 제목은 %d자를 초과할 수 없습니다", MAX_LENGTH));
    }
}
