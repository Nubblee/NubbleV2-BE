package dev.biddan.nubblev2.study.announcement.controller.dto;

import com.blazebit.persistence.PagedList;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementView;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.DifficultyLevel;
import dev.biddan.nubblev2.study.group.domain.StudyGroup.ProgrammingLanguage;
import dev.biddan.nubblev2.study.group.repository.StudyGroupView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;

public class StudyAnnouncementApiResponse {

    public record Basic(
            StudyAnnouncementInfo.Basic studyAnnouncement
    ) {

    }

    public record WithMeta(
            StudyAnnouncementInfo.WithMeta studyAnnouncement
    ) {
    }

    public record Page(
            List<Preview> announcements,
            PageMeta meta
    ) {


        public static Page from(
                PagedList<StudyAnnouncementView> pagedResult,
                Map<Long, List<ProgrammingLanguage>> languagesMap,
                Map<Long, List<DifficultyLevel>> difficultyLevelsMap,
                Map<Long, Long> approvedCountsMap) {

            List<Preview> previews = pagedResult.stream()
                    .map(announcement -> {
                        Long studyGroupId = announcement.studyGroup().id();

                        return Preview.from(
                                announcement,
                                languagesMap.getOrDefault(studyGroupId, List.of()),
                                difficultyLevelsMap.getOrDefault(studyGroupId, List.of()),
                                approvedCountsMap.getOrDefault(announcement.id(), 0L).intValue()
                        );
                    })
                    .toList();

            PageMeta pageMeta = PageMeta.builder()
                    .page(pagedResult.getPage())
                    .totalPages(pagedResult.getTotalPages())
                    .totalSize(pagedResult.getTotalSize())
                    .hasNext(pagedResult.getPage() < pagedResult.getTotalPages())
                    .hasPrevious(pagedResult.getPage() > 1)
                    .build();

            return new Page(previews, pageMeta);
        }
    }

    @Builder
    public record Preview(
            Long id,
            String title,
            Integer recruitCapacity,
            LocalDate endDate,
            String status,
            String closedReason,
            LocalDateTime createdAt,
            LocalDateTime closedAt,
            StudyGroupPreview studyGroup,
            StudyAnnouncementInfo.Meta meta
    ) {

        public static Preview from(
                StudyAnnouncementView view,
                List<ProgrammingLanguage> languages,
                List<DifficultyLevel> difficultyLevels,
                int approvedCount) {

            return Preview.builder()
                    .id(view.id())
                    .title(view.title())
                    .recruitCapacity(view.recruitCapacity())
                    .endDate(view.endDate())
                    .status(view.status() != null ? view.status().toString() : null)
                    .closedReason(view.closedReason() != null ? view.closedReason().toString() : null)
                    .createdAt(view.createdAt())
                    .closedAt(view.closedAt())
                    .studyGroup(StudyGroupPreview.of(view.studyGroup(), languages, difficultyLevels))
                    .meta(new StudyAnnouncementInfo.Meta(approvedCount))
                    .build();
        }
    }

    @Builder
    public record StudyGroupPreview(
            Long id,
            String name,
            String mainLanguage,
            List<String> languages,
            List<String> difficultyLevels,
            int capacity,
            String meetingType,
            String meetingRegion
    ) {

        public static  StudyGroupPreview of(
                StudyGroupView view,
                List<ProgrammingLanguage> languages,
                List<DifficultyLevel> difficultyLevels) {

            List<String> languageNames = languages.stream()
                    .map(ProgrammingLanguage::name)
                    .toList();

            List<String> difficultyLevelNames = difficultyLevels.stream()
                    .map(DifficultyLevel::name)
                    .toList();

            return StudyGroupPreview.builder()
                    .id(view.id())
                    .name(view.name())
                    .mainLanguage(view.mainLanguage().name())
                    .languages(languageNames)
                    .difficultyLevels(difficultyLevelNames)
                    .capacity(view.capacity())
                    .meetingType(view.meetingType().name())
                    .meetingRegion(view.meetingRegion())
                    .build();
        }
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
