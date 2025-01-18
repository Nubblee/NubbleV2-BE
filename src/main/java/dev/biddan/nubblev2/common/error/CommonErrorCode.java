package dev.biddan.nubblev2.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("java:S6548")
public enum CommonErrorCode implements ErrorCode {
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 에러가 발생했습니다."),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "잘못된 인자가 전달되었습니다."),
    UNHANDLED_ERROR(HttpStatus.BAD_REQUEST, "처리되지 않은 예외가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getName() {
        return name();
    }
}
