package dev.biddan.nubblev2.study.group.domain;

import dev.biddan.nubblev2.study.group.domain.StudyGroup.DifficultyLevel;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingDay;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingType;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProblemPlatform;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProgrammingLanguage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudyGroupUpdateBuilder {

    private final List<StudyGroupUpdateCommand> commands = new ArrayList<>();

    public StudyGroupUpdateBuilder name(String name) {
        Optional.ofNullable(name)
                .ifPresent(n -> commands.add(studyGroup -> {
                    studyGroup.updateName(new StudyGroupName(n)); // package-private setter 사용
                }));
        return this;
    }

    public StudyGroupUpdateBuilder description(String description) {
        Optional.ofNullable(description)
                .ifPresent(d -> commands.add(studyGroup -> {
                    studyGroup.updateDescription(new StudyGroupDescription(d));
                }));
        return this;
    }

    public StudyGroupUpdateBuilder capacity(Integer capacity) {
        Optional.ofNullable(capacity)
                .ifPresent(c -> commands.add(studyGroup -> {
                    studyGroup.updateCapacity(new StudyGroupCapacity(c));
                }));
        return this;
    }

    public StudyGroupUpdateBuilder languages(List<ProgrammingLanguage> languages, ProgrammingLanguage mainLanguage) {
        if (languages != null && mainLanguage != null) {
            commands.add(studyGroup -> studyGroup.updateLanguages(new StudyGroupLanguages(languages, mainLanguage)));
        }
        return this;
    }

    public StudyGroupUpdateBuilder difficultyLevels(List<DifficultyLevel> difficultyLevels) {
        Optional.ofNullable(difficultyLevels)
                .ifPresent(levels -> commands.add(studyGroup -> {
                    studyGroup.updateDifficultyLevels(new StudyGroupDifficultyLevels(levels));
                }));
        return this;
    }

    public StudyGroupUpdateBuilder problemPlatforms(List<ProblemPlatform> problemPlatforms) {
        Optional.ofNullable(problemPlatforms)
                .ifPresent(platforms -> commands.add(studyGroup -> {
                    studyGroup.updateProblemPlatforms(new StudyGroupProblemPlatforms(platforms));
                }));
        return this;
    }

    public StudyGroupUpdateBuilder meeting(MeetingType meetingType, String meetingRegion,
            List<MeetingDay> mainMeetingDays) {
        if (meetingType != null && mainMeetingDays != null) {
            commands.add(studyGroup -> studyGroup.updateMeeting(
                    new StudyGroupMeeting(meetingType, meetingRegion, mainMeetingDays)));
        }
        return this;
    }

    public StudyGroupUpdateCommand build() {
        return studyGroup -> commands.forEach(command -> command.execute(studyGroup));
    }

    @FunctionalInterface
    public interface StudyGroupUpdateCommand {

        void execute(StudyGroup studyGroup);
    }
}
