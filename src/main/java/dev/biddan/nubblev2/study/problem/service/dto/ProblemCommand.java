package dev.biddan.nubblev2.study.problem.service.dto;

import dev.biddan.nubblev2.study.group.domain.StudyGroup;
import java.time.LocalDate;

public class ProblemCommand {

    public record Create(
            String title,
            String url,
            LocalDate date,
            StudyGroup studyGroup
    ) {
    }
}