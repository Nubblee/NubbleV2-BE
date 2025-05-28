package dev.biddan.nubblev2.user.controller;

import dev.biddan.nubblev2.user.service.UserService;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
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
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/users")
public class UserApiController {

    private final UserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserApiResponse.Private> register(@RequestBody @Valid UserApiRequest.Register request) {

        UserInfo.Private info = userService.register(request.toCommand());

        UserApiResponse.Private response = new UserApiResponse.Private(info);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
