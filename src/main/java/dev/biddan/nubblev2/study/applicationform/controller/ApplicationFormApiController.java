package dev.biddan.nubblev2.study.applicationform.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.exception.http.BadRequestException;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;
import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormResponse;
import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormResponse.Page;
import dev.biddan.nubblev2.study.applicationform.service.ApplicationFormService;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo.Basic;
import dev.biddan.nubblev2.study.applicationform.service.dto.ApplicationFormInfo.PageList;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @AuthRequired
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplicationFormResponse.Page> findList(
            @PathVariable Long announcementId,
            @CurrentUserId Long currentUserId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(required = false) LocalDateTime lastSubmittedAt,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        if (pageSize < 0 || pageSize > 100) {
            throw new BadRequestException("Page size 범위는 1~100까지 가능합니다");
        }

        PageList formPageList = applicationFormService.findFormsByAnnouncementId(announcementId, currentUserId,
                lastId, lastSubmittedAt, status, pageSize);

        Page response = ApplicationFormResponse.of(formPageList);

        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @PostMapping(
            path = "/{applicationFormId}/approve",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApplicationFormResponse.Basic> approve(
            @PathVariable Long announcementId,
            @PathVariable Long applicationFormId,
            @CurrentUserId Long currentUserId
    ) {
        Basic info = applicationFormService.approve(announcementId, applicationFormId, currentUserId);

        return ResponseEntity.ok(new ApplicationFormResponse.Basic(info));
    }
}
