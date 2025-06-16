package dev.biddan.nubblev2.study.group.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroupUpdateBuilder.StudyGroupUpdateCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "study_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StudyGroupName name;

    @Embedded
    private StudyGroupDescription description;

    @Embedded
    private StudyGroupCapacity capacity;

    @Embedded
    private StudyGroupLanguages languages;

    @Embedded
    private StudyGroupDifficultyLevels difficultyLevels;

    @Embedded
    private StudyGroupProblemPlatforms problemPlatforms;

    @Embedded
    private StudyGroupMeeting meeting;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Builder
    public StudyGroup(String name, String description, Integer capacity,
            List<ProgrammingLanguage> languages, ProgrammingLanguage mainLanguage,
            List<DifficultyLevel> difficultyLevels, List<ProblemPlatform> problemPlatforms,
            MeetingType meetingType, String meetingRegion, List<MeetingDay> mainMeetingDays) {

        this.name = new StudyGroupName(name);
        this.description = new StudyGroupDescription(description);
        this.capacity = new StudyGroupCapacity(capacity);
        this.languages = new StudyGroupLanguages(languages, mainLanguage);
        this.difficultyLevels = new StudyGroupDifficultyLevels(difficultyLevels);
        this.problemPlatforms = new StudyGroupProblemPlatforms(problemPlatforms);
        this.meeting = new StudyGroupMeeting(meetingType, meetingRegion, mainMeetingDays);
    }

    public static StudyGroupUpdateBuilder updateBuilder() {
        return new StudyGroupUpdateBuilder();
    }

    public void applyUpdate(StudyGroupUpdateCommand updateCommand) {
        updateCommand.execute(this);
    }

    void updateName(StudyGroupName name) {
        this.name = name;
    }

    void updateDescription(StudyGroupDescription description) {
        this.description = description;
    }

    void updateCapacity(StudyGroupCapacity capacity) {
        this.capacity = capacity;
    }

    void updateLanguages(StudyGroupLanguages languages) {
        this.languages = languages;
    }

    void updateDifficultyLevels(StudyGroupDifficultyLevels difficultyLevels) {
        this.difficultyLevels = difficultyLevels;
    }

    void updateProblemPlatforms(StudyGroupProblemPlatforms problemPlatforms) {
        this.problemPlatforms = problemPlatforms;
    }

    void updateMeeting(StudyGroupMeeting meeting) {
        this.meeting = meeting;
    }

    public enum ProgrammingLanguage {
        JAVA, PYTHON, JAVASCRIPT, CPP, C, CSHARP, KOTLIN, SWIFT, GO
    }

    public enum DifficultyLevel {
        LV1, LV2, LV3, LV4, LV5
    }

    public enum ProblemPlatform {
        PROGRAMMERS, BAEKJOON, LEET_CODE, CODE_TREE
    }

    public enum MeetingType {
        ONLINE, OFFLINE, HYBRID
    }

    public enum MeetingDay {
        MON, TUE, WED, THU, FRI, SAT, SUN
    }
}
