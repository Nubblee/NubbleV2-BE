package dev.biddan.nubblev2.user.interest.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.user.interest.controller.dto.UserInterestApiRequest;
import dev.biddan.nubblev2.user.interest.controller.dto.UserInterestApiResponse;
import dev.biddan.nubblev2.user.interest.servicce.UserInterestService;
import dev.biddan.nubblev2.user.interest.servicce.dto.UserInterestInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserInterestApiController {

    private final UserInterestService userInterestService;

    @AuthRequired
    @PutMapping(path = "/api/v1/user/interest", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInterestApiResponse.Basic> setInterest(
            @RequestBody UserInterestApiRequest.Set request,
            @CurrentUserId Long currentUserId) {

        UserInterestInfo.Basic info = userInterestService.setInterest(currentUserId, request.toCommand());

        UserInterestApiResponse.Basic response = new UserInterestApiResponse.Basic(info);
        return ResponseEntity.ok(response);
    }
}
