package dev.biddan.nubblev2.study.group.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProgrammingLanguage;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyGroupLanguages {

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "study_group_languages", joinColumns = @JoinColumn(name = "study_group_id"))
    @Column(name = "language")
    private List<ProgrammingLanguage> languages;

    @Enumerated(EnumType.STRING)
    @Column(name = "main_language", nullable = false)
    private ProgrammingLanguage mainLanguage;

    public StudyGroupLanguages(List<ProgrammingLanguage> languages, ProgrammingLanguage mainLanguage) {
        validate(languages, mainLanguage);
        this.languages = languages;
        this.mainLanguage = mainLanguage;
    }

    private void validate(List<ProgrammingLanguage> languages, ProgrammingLanguage mainLanguage) {
        Assert.notEmpty(languages, "프로그래밍 언어는 최소 1개 이상 선택해야 합니다");
        Assert.notNull(mainLanguage, "주 사용 언어는 필수입니다");
        Assert.isTrue(languages.contains(mainLanguage), "주 사용 언어는 선택한 언어 목록에 포함되어야 합니다");
    }

}
