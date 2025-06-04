package dev.biddan.nubblev2.study.announcement.controller.dto;

import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementView;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class StudyAnnouncementApiResponse {

    public record Basic(
            StudyAnnouncementInfo.Basic studyAnnouncement
    ) {

    }

    public record PagedList(
            List<Summary> announcements,
            PageMeta meta
    ) {


        public static PagedList from(com.blazebit.persistence.PagedList<StudyAnnouncementView> pagedResult) {
            List<Summary> summaries = pagedResult.stream()
                    .map(Summary::from)
                    .toList();

            PageMeta pageMeta = PageMeta.builder()
                    .page(pagedResult.getPage())
                    .totalPages(pagedResult.getTotalPages())
                    .totalSize(pagedResult.getTotalSize())
                    .hasNext(pagedResult.getPage() < pagedResult.getTotalPages())
                    .hasPrevious(pagedResult.getPage() > 1)
                    .build();

            return new PagedList(summaries, pageMeta);
        }
    }

    @Builder
    public record Summary(
            Long id,
            String title,
            String description,
            Integer recruitCapacity,
            LocalDateTime startDateTime,
            LocalDate endDate,
            String status,
            String closedReason,
            LocalDateTime createdAt,
            LocalDateTime closedAt,
            StudyGroupSummary studyGroup,
            CreatorSummary creator
    ) {

        public static Summary from(StudyAnnouncementView announcement) {
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
                    .creator(new CreatorSummary(announcement.creatorId(), announcement.creatorNickname()))
                    .build();
        }
    }

    public record StudyGroupSummary(
            Long id,
            String name
    ) {

    }

    public record CreatorSummary(
            Long id,
            String nickname
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
