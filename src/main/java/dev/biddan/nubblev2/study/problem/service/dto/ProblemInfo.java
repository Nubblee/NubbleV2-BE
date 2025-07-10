package dev.biddan.nubblev2.study.problem.service.dto;

import dev.biddan.nubblev2.study.problem.domain.Problem;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProblemInfo(
        Long id,
        String title,
        String url,
        LocalDate date,
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
                problem.getCreatedBy().getId(),
                problem.getStudyGroup().getId(),
                problem.getCreatedAt()
        );
    }
}