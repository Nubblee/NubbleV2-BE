package dev.biddan.nubblev2.study.applicationform.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;
import dev.biddan.nubblev2.study.applicationform.service.ApplicationFormService;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo.Basic;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/study-announcements/{announcementId}/application-forms")
@RequiredArgsConstructor
public class ApplicationFormApiController {

    private final ApplicationFormService applicationFormService;

    @AuthRequired
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplicationFormResponse.Basic> submit(
            @PathVariable Long announcementId,
            @RequestBody @Valid ApplicationFormApiRequest.Submit request,
            @CurrentUserId Long currentUserId) {

        Basic info = applicationFormService.submit(announcementId, currentUserId, request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApplicationFormResponse.Basic(info));
    }

    public class ApplicationFormResponse {

        public record Basic(
                ApplicationFormInfo.Basic applicationForm
        ) {

        }
    }
}
