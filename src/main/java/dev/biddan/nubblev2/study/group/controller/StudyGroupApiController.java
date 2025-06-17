package dev.biddan.nubblev2.study.group.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.study.group.controller.StudyGroupApiResponse.Detail;
import dev.biddan.nubblev2.study.group.service.StudyGroupService;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/study-groups")
@RequiredArgsConstructor
public class StudyGroupApiController {

    private final StudyGroupService studyGroupService;

    @AuthRequired
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Detail> create(
            @RequestBody @Valid StudyGroupApiRequest.Create request,
            @CurrentUserId Long currentId
    ) {
        StudyGroupInfo.Detail info = studyGroupService.create(currentId, request.toCreateCommand());

        Detail response = new Detail(info);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @AuthRequired
    @PatchMapping(path = "/{studyGroupId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Detail> update(
            @RequestBody @Valid StudyGroupApiRequest.Create request,
            @CurrentUserId Long currentId,
            @PathVariable Long studyGroupId
    ) {
        StudyGroupInfo.Detail info = studyGroupService.update(studyGroupId, currentId, request.toCreateCommand());

        Detail response = new Detail(info);

        return ResponseEntity.ok(response);
    }
}
