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
public class AnnouncementApplicationForm {

    private static final int MAX_LENGTH = 5000;

    @Column(name = "application_form_content", length = MAX_LENGTH)
    private String content;

    public AnnouncementApplicationForm(String content) {
        validate(content);
        this.content = content;
    }

    private void validate(String content) {
        Assert.hasText(content, "신청서 양식 내용은 필수입니다");
        Assert.isTrue(content.length() <= MAX_LENGTH,
                String.format("신청서 양식은 %d자를 초과할 수 없습니다", MAX_LENGTH));
    }
}

