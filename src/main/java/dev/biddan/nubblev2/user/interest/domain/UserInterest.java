package dev.biddan.nubblev2.user.interest.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup.DifficultyLevel;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProblemPlatform;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProgrammingLanguage;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

@Entity
@Table(name = "user_interests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class UserInterest {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_interested_languages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "language")
    private final List<ProgrammingLanguage> interestedLanguages = new ArrayList<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_current_levels", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "level")
    private final List<DifficultyLevel> currentLevels = new ArrayList<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_preferred_platforms", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "platform")
    private final List<ProblemPlatform> preferredPlatforms = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public UserInterest(Long userId, List<ProgrammingLanguage> interestedLanguages,
            List<DifficultyLevel> currentLevels, List<ProblemPlatform> preferredPlatforms) {
        validate(userId, interestedLanguages, currentLevels, preferredPlatforms);

        this.userId = userId;
        this.interestedLanguages.addAll(interestedLanguages);
        this.currentLevels.addAll(currentLevels);
        this.preferredPlatforms.addAll(preferredPlatforms);
    }

    public void update(List<ProgrammingLanguage> interestedLanguages,
            List<DifficultyLevel> targetLevels, List<ProblemPlatform> preferredPlatforms) {
        validate(this.userId, interestedLanguages, targetLevels, preferredPlatforms);

        this.interestedLanguages.clear();
        this.interestedLanguages.addAll(interestedLanguages);

        this.currentLevels.clear();
        this.currentLevels.addAll(targetLevels);

        this.preferredPlatforms.clear();
        this.preferredPlatforms.addAll(preferredPlatforms);
    }

    private void validate(Long userId, List<ProgrammingLanguage> interestedLanguages,
            List<DifficultyLevel> targetLevels, List<ProblemPlatform> preferredPlatforms) {
        Assert.notNull(userId, "사용자 ID는 필수입니다");
        Assert.notEmpty(interestedLanguages, "관심 언어는 최소 1개 이상 선택해야 합니다");
        Assert.notEmpty(targetLevels, "현재 레벨은 최소 1개 이상 선택해야 합니다");
        Assert.notEmpty(preferredPlatforms, "선호 플랫폼은 최소 1개 이상 선택해야 합니다");
    }
}
