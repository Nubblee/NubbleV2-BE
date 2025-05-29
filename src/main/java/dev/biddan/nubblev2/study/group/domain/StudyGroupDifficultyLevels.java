package dev.biddan.nubblev2.study.group.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup.DifficultyLevel;
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
public class StudyGroupDifficultyLevels {

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "study_group_difficulty_levels", joinColumns = @JoinColumn(name = "study_group_id"))
    @Column(name = "difficulty_level")
    private List<DifficultyLevel> values;

    public StudyGroupDifficultyLevels(List<DifficultyLevel> values) {
        validate(values);
        this.values = values;
    }

    private void validate(List<DifficultyLevel> values) {
        Assert.notEmpty(values, "난이도는 최소 1개 이상 선택해야 합니다");
    }
}
