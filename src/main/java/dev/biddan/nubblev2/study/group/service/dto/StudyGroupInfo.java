package dev.biddan.nubblev2.study.group.service.dto;

import com.blazebit.persistence.PagedList;
import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import dev.biddan.nubblev2.study.group.repository.StudyGroupView;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Builder;

public class StudyGroupInfo {

    @Builder
    public record Detail(
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

        public static Detail from(StudyGroup studyGroup) {
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

            return Detail.builder()
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

    public record PageList(
            List<Preview> studyGroups,
            PageMeta meta
    ) {

        public static PageList of(
                PagedList<StudyGroupView> pagedResult,
                Map<Long, List<StudyGroup.DifficultyLevel>> difficultyLevelsMap,
                Map<Long, List<StudyGroup.MeetingDay>> meetingDaysMap,
                Map<Long, Long> memberCountsMap) {

            List<Preview> previews = pagedResult.stream()
                    .map(studyGroup -> Preview.of(
                            studyGroup,
                            difficultyLevelsMap.getOrDefault(studyGroup.id(), Collections.emptyList()),
                            meetingDaysMap.getOrDefault(studyGroup.id(), Collections.emptyList()),
                            memberCountsMap.getOrDefault(studyGroup.id(), 0L).intValue()
                    ))
                    .toList();

            PageMeta pageMeta = PageMeta.builder()
                    .page(pagedResult.getPage())
                    .totalPages(pagedResult.getTotalPages())
                    .totalSize(pagedResult.getTotalSize())
                    .hasNext(pagedResult.getPage() < pagedResult.getTotalPages())
                    .hasPrevious(pagedResult.getPage() > 1)
                    .build();

            return new PageList(previews, pageMeta);
        }
    }

    @Builder
    public record Preview(
            Long id,
            String name,
            String mainLanguage,
            Integer capacity,
            String meetingType,
            String meetingRegion,
            List<String> difficultyLevels,
            List<String> mainMeetingDays,
            PreviewMeta meta
    ) {

        public static Preview of(
                StudyGroupView view,
                List<StudyGroup.DifficultyLevel> difficultyLevels,
                List<StudyGroup.MeetingDay> meetingDays,
                int currentMemberCount) {

            List<String> difficultyLevelNames = difficultyLevels.stream()
                    .map(StudyGroup.DifficultyLevel::name)
                    .toList();

            List<String> meetingDayNames = meetingDays.stream()
                    .map(StudyGroup.MeetingDay::name)
                    .toList();

            return Preview.builder()
                    .id(view.id())
                    .name(view.name())
                    .mainLanguage(view.mainLanguage().name())
                    .capacity(view.capacity())
                    .meetingType(view.meetingType().name())
                    .meetingRegion(view.meetingRegion())
                    .difficultyLevels(difficultyLevelNames)
                    .mainMeetingDays(meetingDayNames)
                    .meta(new PreviewMeta(currentMemberCount))
                    .build();
        }
    }

    public record PreviewMeta(
            int currentMemberCount
    ) {

    }

    @Builder
    public record PageMeta(
            Integer page,
            Integer totalPages,
            Long totalSize,
            Boolean hasNext,
            Boolean hasPrevious
    ) {

    }
}
