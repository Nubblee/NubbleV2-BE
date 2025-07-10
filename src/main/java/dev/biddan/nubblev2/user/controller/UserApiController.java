package dev.biddan.nubblev2.user.controller;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import dev.biddan.nubblev2.auth.service.AuthSessionCreator;
import dev.biddan.nubblev2.auth.service.AuthSessionInfo;
import dev.biddan.nubblev2.exception.http.BadRequestException;
import dev.biddan.nubblev2.http.AuthSessionCookieManager;
import dev.biddan.nubblev2.http.HttpIpExtractor;
import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import dev.biddan.nubblev2.study.group.service.StudyGroupService;
import dev.biddan.nubblev2.study.group.service.dto.StudyGroupInfo;
import dev.biddan.nubblev2.user.controller.dto.AvailabilityApiResponse;
import dev.biddan.nubblev2.user.controller.dto.UserApiRequest;
import dev.biddan.nubblev2.user.controller.dto.UserApiResponse;
import dev.biddan.nubblev2.user.repository.UserRepository;
import dev.biddan.nubblev2.user.service.UserService;
import dev.biddan.nubblev2.user.service.dto.UserInfo;
import dev.biddan.nubblev2.user.service.dto.UserInfo.Private;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final HttpIpExtractor httpIpExtractor;
    private final AuthSessionCreator authSessionCreator;
    private final AuthSessionCookieManager authSessionCookieManager;
    private final StudyGroupService studyGroupService;

    @PostMapping(
            path = "/api/v1/users",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApiResponse.Private> register(
            @RequestBody @Valid UserApiRequest.Register request,
            HttpServletRequest httpServletRequest) {

        UserInfo.Private userInfo = userService.register(request.toRegisterCommand());

        String clientIp = httpIpExtractor.getClientIpAddress(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");

        AuthSessionInfo.Basic authSessionInfo = authSessionCreator.create(userInfo, clientIp, userAgent);

        ResponseCookie sessionCookie = authSessionCookieManager.createSessionCookie(authSessionInfo.sessionId());

        UserApiResponse.Private response = new UserApiResponse.Private(userInfo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(response);
    }

    @AuthRequired
    @GetMapping(path = "/api/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApiResponse.Private> getUser(@CurrentUserId Long userId) {
        Private userInfo = userService.getUserById(userId);

        return  ResponseEntity.ok(new UserApiResponse.Private(userInfo));
    }

    @GetMapping(path = "/api/v1/availability/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AvailabilityApiResponse> checkAvailability(
            @RequestParam(required = false) String loginId,
            @RequestParam(required = false) String nickname
    ) {
        boolean hasNickname = nickname != null && !nickname.isBlank();
        boolean hasLoginId = loginId != null && !loginId.isBlank();
        if (!hasLoginId && !hasNickname) {
            throw new BadRequestException("1개의 파라미터 값은 필수입니다");
        }
        if (hasLoginId && hasNickname) {
            throw new BadRequestException("1개의 파라미터만 가능합니다");
        }

        AvailabilityApiResponse response = AvailabilityApiResponse.available();

        if (hasNickname) {
            if (userRepository.existsByNickname(nickname)) {
                response = AvailabilityApiResponse.notAvailable("ALREADY_EXISTS", "이미 사용 중인 닉네임입니다");
            }
        } else {
            if (userRepository.existsByLoginId(loginId)) {
                response = AvailabilityApiResponse.notAvailable("ALREADY_EXISTS", "이미 사용 중인 로그인 ID입니다");
            }
        }

        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @GetMapping("/api/v1/user/study-groups")
    public ResponseEntity<StudyGroupInfo.PageList> findMyStudyGroups(@CurrentUserId Long currentUserId) {
        StudyGroupInfo.PageList response = studyGroupService.findStudyGroupsByUserId(currentUserId);

        return ResponseEntity.ok(response);
    }
}
