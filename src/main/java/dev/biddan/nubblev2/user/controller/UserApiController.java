package dev.biddan.nubblev2.user.controller;

import dev.biddan.nubblev2.user.service.UserService;
import dev.biddan.nubblev2.user.service.UserService.UserRegisterCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping(value = "/api/users",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterCommand request) {
        Long newUserId = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserRegisterResponse(newUserId));
    }

    public record UserRegisterResponse(
            Long newUserId
    ) {

    }
}
