package dev.biddan.nubblev2.common.error;

import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;

public record ApiErrorResponse(
        String errorCode,
        String errorMessage,
        LocalDateTime timestamp
) {

    public static ApiErrorResponse of(String code, String errorMessage) {
        return new ApiErrorResponse(code, errorMessage, LocalDateTime.now());
    }

    public static ResponseEntity<ApiErrorResponse> toResponseEntity(ErrorCode errorCode, String errorMessage) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiErrorResponse.of(errorCode.getName(), errorMessage));
    }
}
