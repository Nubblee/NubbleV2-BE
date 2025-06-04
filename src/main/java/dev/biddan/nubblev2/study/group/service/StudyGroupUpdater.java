package dev.biddan.nubblev2.study.group.service;

import dev.biddan.nubblev2.exception.http.BadRequestException;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.DifficultyLevel;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingDay;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingType;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProblemPlatform;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProgrammingLanguage;
import dev.biddan.nubblev2.study.group.domain.StudyGroupUpdateBuilder.StudyGroupUpdateCommand;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupCommand;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyGroupUpdater {

    @Transactional
    public void update(StudyGroup studyGroup, StudyGroupCommand.Create command) {
        studyGroup.applyUpdate(buildUpdateCommand(command));
    }

    private StudyGroupUpdateCommand buildUpdateCommand(StudyGroupCommand.Create command) {
        return StudyGroup.updateBuilder()
                .name(command.name())
                .description(command.description())
                .capacity(command.capacity())
                .languages(parseLanguages(command.languages()), parseMainLanguage(command.mainLanguage()))
                .difficultyLevels(parseDifficultyLevels(command.difficultyLevels()))
                .problemPlatforms(parseProblemPlatforms(command.problemPlatforms()))
                .meeting(parseMeetingType(command.meetingType()), command.meetingRegion(),
                        parseMeetingDays(command.mainMeetingDays()))
                .build();
    }

    @SuppressWarnings("java:S1168")
    private List<ProgrammingLanguage> parseLanguages(List<String> languages) {
        if (languages == null) {
            return null;
        }
        return languages.stream()
                .map(this::parseLanguage)
                .toList();
    }

    @SuppressWarnings("java:S1168")
    private ProgrammingLanguage parseMainLanguage(String mainLanguage) {
        if (mainLanguage == null) {
            return null;
        }
        return parseLanguage(mainLanguage);
    }

    private ProgrammingLanguage parseLanguage(String language) {
        try {
            return ProgrammingLanguage.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("유효하지 않은 프로그래밍 언어입니다: " + language);
        }
    }

    @SuppressWarnings("java:S1168")
    private List<DifficultyLevel> parseDifficultyLevels(List<String> difficultyLevels) {
        if (difficultyLevels == null) {
            return null;
        }
        return difficultyLevels.stream()
                .map(level -> {
                    try {
                        return DifficultyLevel.valueOf(level.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestException("유효하지 않은 난이도입니다: " + level);
                    }
                })
                .toList();
    }

    @SuppressWarnings("java:S1168")
    private List<ProblemPlatform> parseProblemPlatforms(List<String> problemPlatforms) {
        if (problemPlatforms == null) {
            return null;
        }
        return problemPlatforms.stream()
                .map(platform -> {
                    try {
                        return ProblemPlatform.valueOf(platform.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestException("유효하지 않은 문제 플랫폼입니다: " + platform);
                    }
                })
                .toList();
    }

    private MeetingType parseMeetingType(String meetingType) {
        if (meetingType == null) {
            return null;
        }
        try {
            return MeetingType.valueOf(meetingType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("유효하지 않은 모임 형태입니다: " + meetingType);
        }
    }

    @SuppressWarnings("java:S1168")
    private List<MeetingDay> parseMeetingDays(List<String> meetingDays) {
        if (meetingDays == null) {
            return null;
        }
        return meetingDays.stream()
                .map(day -> {
                    try {
                        return MeetingDay.valueOf(day.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestException("유효하지 않은 요일입니다: " + day);
                    }
                })
                .toList();
    }
}
