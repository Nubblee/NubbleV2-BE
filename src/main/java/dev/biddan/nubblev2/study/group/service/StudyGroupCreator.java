package dev.biddan.nubblev2.study.group.service;

import dev.biddan.nubblev2.exception.http.BadRequestException;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.DifficultyLevel;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingDay;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.MeetingType;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProblemPlatform;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProgrammingLanguage;
import dev.biddan.nubblev2.study.group.repository.StudyGroupRepository;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupCommand.Create;
import dev.biddan.nubblev2.user.domain.User;
import dev.biddan.nubblev2.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyGroupCreator {

    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;

    @Transactional
    public StudyGroup create(User creator, Create command) {
        StudyGroup newStudyGroup = StudyGroup.builder()
                .name(command.name())
                .description(command.description())
                .capacity(command.capacity())
                .startDate(command.startDate())
                .endDate(command.endDate())
                .languages(parseLanguages(command.languages()))
                .mainLanguage(parseMainLanguage(command.mainLanguage()))
                .difficultyLevels(parseDifficultyLevels(command.difficultyLevels()))
                .problemPlatforms(parseProblemPlatforms(command.problemPlatforms()))
                .meetingType(parseMeetingType(command.meetingType()))
                .meetingRegion(command.meetingRegion())
                .mainMeetingDays(parseMeetingDays(command.mainMeetingDays()))
                .creator(creator)
                .build();

        return studyGroupRepository.save(newStudyGroup);
    }

    private List<ProgrammingLanguage> parseLanguages(List<String> languages) {
        return languages.stream()
                .map(this::parseLanguage)
                .toList();
    }

    private ProgrammingLanguage parseMainLanguage(String mainLanguage) {
        return parseLanguage(mainLanguage);
    }

    private ProgrammingLanguage parseLanguage(String language) {
        try {
            return ProgrammingLanguage.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("유효하지 않은 프로그래밍 언어입니다: " + language);
        }
    }

    private List<DifficultyLevel> parseDifficultyLevels(List<String> difficultyLevels) {
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

    private List<ProblemPlatform> parseProblemPlatforms(List<String> problemPlatforms) {
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
        try {
            return MeetingType.valueOf(meetingType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("유효하지 않은 모임 형태입니다: " + meetingType);
        }
    }

    private List<MeetingDay> parseMeetingDays(List<String> meetingDays) {
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
