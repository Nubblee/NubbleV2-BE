package dev.biddan.nubblev2.study.problem.service.dto;

import dev.biddan.nubblev2.study.problem.domain.Problem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record ProblemInfo(
        Long id,
        String title,
        String url,
        LocalDate date,
        String tag,
        Long createdBy,
        Long studyGroupId,
        LocalDateTime createdAt
) {

    public static ProblemInfo from(Problem problem) {
        return new ProblemInfo(
                problem.getId(),
                problem.getTitle(),
                problem.getUrl(),
                problem.getDate(),
                problem.getTag(),
                problem.getCreatedBy().getId(),
                problem.getStudyGroup().getId(),
                problem.getCreatedAt()
        );
    }

    public record PageList(
            List<ProblemInfo> problems,
            PageMeta meta
    ) {

        public static PageList of(List<ProblemInfo> problems, int page, int limit, long totalCount) {
            int totalPages = (int) Math.ceil((double) totalCount / limit);
            
            PageMeta pageMeta = PageMeta.builder()
                    .page(page)
                    .totalPages(totalPages)
                    .totalSize(totalCount)
                    .hasNext(page < totalPages)
                    .hasPrevious(page > 1)
                    .build();

            return new PageList(problems, pageMeta);
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
