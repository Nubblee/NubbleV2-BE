package dev.biddan.nubblev2.study.announcement.controller.dto;

import com.blazebit.persistence.PagedList;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementView;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo;
import dev.biddan.nubblev2.study.applicationform.domain.StudyApplicationForm;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo;
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
            List<Summary> announcements,
            PageMeta meta
    ) {


        public static Page from(
                PagedList<StudyAnnouncementView> pagedResult,
                Map<Long, Long> approvedCountsMap) {
            List<Summary> summaries = pagedResult.stream()
                    .map(announcement -> Summary.from(
                            announcement,
                            approvedCountsMap.getOrDefault(announcement.id(), 0L).intValue()
                    ))
                    .toList();

            PageMeta pageMeta = PageMeta.builder()
                    .page(pagedResult.getPage())
                    .totalPages(pagedResult.getTotalPages())
                    .totalSize(pagedResult.getTotalSize())
                    .hasNext(pagedResult.getPage() < pagedResult.getTotalPages())
                    .hasPrevious(pagedResult.getPage() > 1)
                    .build();

            return new Page(summaries, pageMeta);
        }
    }

    @Builder
    public record Summary(
            Long id,
            String title,
            String description,
            Integer recruitCapacity,
            LocalDate endDate,
            String status,
            String closedReason,
            LocalDateTime createdAt,
            LocalDateTime closedAt,
            StudyGroupSummary studyGroup,
            StudyAnnouncementInfo.Meta meta
    ) {

        public static Summary from(StudyAnnouncementView announcement, int approvedCount) {
            return Summary.builder()
                    .id(announcement.id())
                    .title(announcement.title())
                    .description(announcement.description())
                    .recruitCapacity(announcement.recruitCapacity())
                    .endDate(announcement.endDate())
                    .status(announcement.status() != null ? announcement.status().toString() : null)
                    .closedReason(announcement.closedReason() != null ? announcement.closedReason().toString() : null)
                    .createdAt(announcement.createdAt())
                    .closedAt(announcement.closedAt())
                    .studyGroup(new StudyGroupSummary(announcement.studyGroupId(), announcement.studyGroupName()))
                    .meta(new StudyAnnouncementInfo.Meta(approvedCount))
                    .build();
        }
    }

    public record StudyGroupSummary(
            Long id,
            String name
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
