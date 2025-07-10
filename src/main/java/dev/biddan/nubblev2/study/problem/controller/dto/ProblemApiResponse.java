package dev.biddan.nubblev2.study.problem.controller.dto;

import dev.biddan.nubblev2.study.problem.service.dto.ProblemInfo;

public class ProblemApiResponse {

    public record Create(
            ProblemInfo problem
    ) {
    }
}