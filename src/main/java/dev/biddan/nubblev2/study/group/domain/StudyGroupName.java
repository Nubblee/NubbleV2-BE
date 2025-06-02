package dev.biddan.nubblev2.study.group.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyGroupName {

    private static final int MAX_LENGTH = 50;

    @Column(name = "nickname", nullable = false, length = MAX_LENGTH)
    private String value;

    public StudyGroupName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        Assert.hasText(value, "스터디 그룹 이름은 필수입니다");
        Assert.isTrue(value.length() <= MAX_LENGTH,
                String.format("스터디 그룹 이름은 %d자를 초과할 수 없습니다", MAX_LENGTH));
    }
}
