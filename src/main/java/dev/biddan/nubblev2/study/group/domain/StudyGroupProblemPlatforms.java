package dev.biddan.nubblev2.study.group.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProblemPlatform;
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
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroupProblemPlatforms {

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "study_group_problem_platforms", joinColumns = @JoinColumn(name = "study_group_id"))
    @Column(name = "problem_platform")
    private List<ProblemPlatform> values;

    public StudyGroupProblemPlatforms(List<ProblemPlatform> values) {
        validate(values);
        this.values = values;
    }

    private void validate(List<ProblemPlatform> values) {
        Assert.notEmpty(values, "문제 플랫폼은 최소 1개 이상 선택해야 합니다");
    }
}
