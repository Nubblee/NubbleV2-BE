package dev.biddan.nubblev2.user.error;

import dev.biddan.nubblev2.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("java:S6548")
public enum UserErrorCode implements ErrorCode {
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getName() {
        return name();
    }
}
