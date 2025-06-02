package dev.biddan.nubblev2.study.announcement.controller;

import com.blazebit.persistence.PagedList;
import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiRequest;
import dev.biddan.nubblev2.study.announcement.controller.dto.StudyAnnouncementApiResponse;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementBlazeRepository;
import dev.biddan.nubblev2.study.announcement.repository.StudyAnnouncementView;
import dev.biddan.nubblev2.study.announcement.service.StudyAnnouncementService;
import dev.biddan.nubblev2.study.announcement.service.dto.StudyAnnouncementInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/study-announcements")
@RequiredArgsConstructor
public class StudyAnnouncementApiController {

    private final StudyAnnouncementService studyAnnouncementService;
    private final StudyAnnouncementBlazeRepository studyAnnouncementBlazeRepository;

    @AuthRequired
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudyAnnouncementApiResponse.Basic> create(
            @CurrentUserId Long currentUserId,
            @RequestBody StudyAnnouncementApiRequest.Create request
    ) {

        StudyAnnouncementInfo.Basic info = studyAnnouncementService.create(request.studyGroupId(), currentUserId,
                request.toCreateCommand());

        StudyAnnouncementApiResponse.Basic response = new StudyAnnouncementApiResponse.Basic(info);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudyAnnouncementApiResponse.PagedList> findList(
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        page = page < 1 ? 1 : page;
        size = size < 1 ? 1 : size;

        PagedList<StudyAnnouncementView> announcements = studyAnnouncementBlazeRepository.findAnnouncements(statuses,
                page, size);

        return ResponseEntity.ok(StudyAnnouncementApiResponse.PagedList.from(announcements));
    }
}
