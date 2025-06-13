package dev.biddan.nubblev2.user.interest.servicce;

import dev.biddan.nubblev2.exception.http.UnprocessableEntityException;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.DifficultyLevel;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProblemPlatform;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProgrammingLanguage;
import dev.biddan.nubblev2.user.interest.domain.UserInterest;
import dev.biddan.nubblev2.user.interest.repository.UserInterestRepository;
import dev.biddan.nubblev2.user.interest.servicce.dto.UserInterestCommand;
import dev.biddan.nubblev2.user.interest.servicce.dto.UserInterestInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;

    @Transactional
    public UserInterestInfo.Basic setInterest(Long userId, UserInterestCommand.Set command) {
        List<ProgrammingLanguage> languages = parseLanguages(command.interestedLanguages());
        List<DifficultyLevel> levels = parseLevels(command.currentLevels());
        List<ProblemPlatform> platforms = parsePlatforms(command.preferredPlatforms());

        UserInterest userInterest = userInterestRepository.findById(userId)
                .map(existing -> {
                    existing.update(languages, levels, platforms);
                    return existing;
                })
                .orElseGet(() -> UserInterest.builder()
                        .userId(userId)
                        .interestedLanguages(languages)
                        .currentLevels(levels)
                        .preferredPlatforms(platforms)
                        .build());

        UserInterest savedInterest = userInterestRepository.save(userInterest);
        return UserInterestInfo.Basic.of(savedInterest);
    }

    private List<ProgrammingLanguage> parseLanguages(List<String> languages) {
        return languages.stream()
                .map(this::parseLanguage)
                .toList();
    }

    private ProgrammingLanguage parseLanguage(String language) {
        try {
            return ProgrammingLanguage.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnprocessableEntityException("유효하지 않은 프로그래밍 언어입니다: " + language);
        }
    }

    private List<DifficultyLevel> parseLevels(List<String> levels) {
        return levels.stream()
                .map(this::parseLevel)
                .toList();
    }

    private DifficultyLevel parseLevel(String level) {
        try {
            return DifficultyLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnprocessableEntityException("유효하지 않은 난이도입니다: " + level);
        }
    }

    private List<ProblemPlatform> parsePlatforms(List<String> platforms) {
        return platforms.stream()
                .map(this::parsePlatform)
                .toList();
    }

    private ProblemPlatform parsePlatform(String platform) {
        try {
            return ProblemPlatform.valueOf(platform.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnprocessableEntityException("유효하지 않은 문제 플랫폼입니다: " + platform);
        }
    }
}
