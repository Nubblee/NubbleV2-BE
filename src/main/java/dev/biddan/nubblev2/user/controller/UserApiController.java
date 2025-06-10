package dev.biddan.nubblev2.user.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.user.repository.UserRepository;
import dev.biddan.nubblev2.user.service.UserService;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import dev.biddan.nubblev2.user.service.dto.UserInfo.Private;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping(
            path = "/api/v1/users",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserApiResponse.Private> register(@RequestBody @Valid UserApiRequest.Register request) {

        UserInfo.Private info = userService.register(request.toRegisterCommand());

        UserApiResponse.Private response = new UserApiResponse.Private(info);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @AuthRequired
    @GetMapping(path = "/api/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserApiResponse.Private> getUser(@CurrentUserId Long userId) {
        Private userInfo = userService.getUserById(userId);

        return  ResponseEntity.ok(new UserApiResponse.Private(userInfo));
    }
}
