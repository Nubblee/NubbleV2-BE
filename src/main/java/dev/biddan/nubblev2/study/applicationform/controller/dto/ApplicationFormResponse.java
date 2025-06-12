package dev.biddan.nubblev2.study.applicationform.controller.dto;

import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo.Basic;
import java.time.LocalDateTime;
import java.util.List;

public class ApplicationFormResponse {

    public record Basic(
            ApplicationFormInfo.Basic applicationForm
    ) {

    }

    public record PageMeta(
            boolean hasNext,
            Long lastId,
            LocalDateTime lastSubmittedAt
    ) {

    }

    public record Page(
            List<ApplicationFormInfo.Basic> applicationForms,
            PageMeta meta
    ) {

    }

    public static Page of(ApplicationFormInfo.PageList formsInfo) {
        return new Page(
                formsInfo.forms(),
                new PageMeta(formsInfo.hasNext(), formsInfo.lastId(), formsInfo.lastSubmittedAt())
        );
    }
}
