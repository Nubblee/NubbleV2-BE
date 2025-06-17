package dev.biddan.nubblev2.study.group.service.dto;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public class StudyGroupInfo {

    @Builder
    public record Private(
            Long id,
            String name,
            String description,
            Integer capacity,
            List<String> languages,
            String mainLanguage,
            List<String> difficultyLevels,
            List<String> problemPlatforms,
            String meetingType,
            String meetingRegion,
            List<String> mainMeetingDays
    ) {

        public static Private from(StudyGroup studyGroup) {
            return Private.builder()
                    .id(studyGroup.getId())
                    .name(studyGroup.getName().getValue())
                    .description(studyGroup.getDescription().getValue())
                    .capacity(studyGroup.getCapacity().getValue())
                    .languages(studyGroup.getLanguages().getLanguages().stream()
                            .map(Enum::name)
                            .toList())
                    .mainLanguage(studyGroup.getLanguages().getMainLanguage().name())
                    .difficultyLevels(studyGroup.getDifficultyLevels().getValues().stream()
                            .map(Enum::name)
                            .toList())
                    .problemPlatforms(studyGroup.getProblemPlatforms().getValues().stream()
                            .map(Enum::name)
                            .toList())
                    .meetingType(studyGroup.getMeeting().getMeetingType().name())
                    .meetingRegion(studyGroup.getMeeting().getMeetingRegion())
                    .mainMeetingDays(studyGroup.getMeeting().getMainMeetingDays().stream()
                            .map(Enum::name)
                            .toList())
                    .build();
        }
    }

    @Builder
    public record GroupDetail(
            Long id,
            String name,
            String description,
            Integer capacity,
            List<String> languages,
            String mainLanguage,
            List<String> difficultyLevels,
            List<String> problemPlatforms,
            String meetingType,
            String meetingRegion,
            List<String> mainMeetingDays
    ) {

        public static GroupDetail from(StudyGroup studyGroup) {
            List<String> difficultyLevels = studyGroup.getDifficultyLevels().getValues().stream()
                    .map(Enum::name)
                    .toList();
            List<String> problemPlatforms = studyGroup.getProblemPlatforms().getValues().stream()
                    .map(Enum::name)
                    .toList();
            List<String> meetingDays = studyGroup.getMeeting().getMainMeetingDays().stream()
                    .map(Enum::name)
                    .toList();
            List<String> languages = studyGroup.getLanguages().getLanguages().stream()
                    .map(Enum::name)
                    .toList();

            return GroupDetail.builder()
                    .id(studyGroup.getId())
                    .name(studyGroup.getName().getValue())
                    .description(studyGroup.getDescription().getValue())
                    .capacity(studyGroup.getCapacity().getValue())
                    .languages(languages)
                    .mainLanguage(studyGroup.getLanguages().getMainLanguage().name())
                    .difficultyLevels(difficultyLevels)
                    .problemPlatforms(problemPlatforms)
                    .meetingType(studyGroup.getMeeting().getMeetingType().name())
                    .meetingRegion(studyGroup.getMeeting().getMeetingRegion())
                    .mainMeetingDays(meetingDays)
                    .build();
        }
    }
}
