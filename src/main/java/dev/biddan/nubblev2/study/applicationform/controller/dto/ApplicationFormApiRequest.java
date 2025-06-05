package dev.biddan.nubblev2.study.applicationform.controller.dto;

import dev.biddan.nubblev2.study.applicationform.service.ApplicationFormService.ApplicationFormCommand;

public class ApplicationFormApiRequest {

    public record Submit(
            String content
    ) {

        public ApplicationFormCommand.Submit toCommand() {
            return new  ApplicationFormCommand.Submit(content);
        }
    }
}
