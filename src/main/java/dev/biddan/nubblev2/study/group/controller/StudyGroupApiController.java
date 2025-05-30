package dev.biddan.nubblev2.study.group.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;
import dev.biddan.nubblev2.study.group.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<StudyGroupApiResponse.Private> create(
            @RequestBody @Valid StudyGroupApiRequest.Create request,
            @CurrentUserId Long currentId
    ) {
        StudyGroupInfo.Private info = studyGroupService.create(request.toCreateCommand(currentId));

        StudyGroupApiResponse.Private response = new StudyGroupApiResponse.Private(info);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
