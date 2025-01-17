package dev.biddan.nubblev2.common.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(final IllegalArgumentException exception) {
        return ApiErrorResponse.toResponseEntity(
                CommonErrorCode.INVALID_ARGUMENT, exception.getMessage());
    }

    @ExceptionHandler(BaseException.class)
    ResponseEntity<ApiErrorResponse> handleBaseException(
            final BaseException exception,
            final HttpServletRequest request) {
        log.warn("[UNHANDLED_ERROR] Exception Type: {}, Request URI: {}, Message: {}",
                exception.getClass().getSimpleName(),
                request.getRequestURI(),
                exception.getMessage(),
                exception);

        return ApiErrorResponse.toResponseEntity(
                CommonErrorCode.UNHANDLED_ERROR, exception.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(
            final NoResourceFoundException exception) {
        return ApiErrorResponse.toResponseEntity(
                CommonErrorCode.NOT_FOUND,
                String.format("요청하신 URL: `/%s`을(를) 찾을 수 없습니다.", exception.getResourcePath()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleException(
            final Exception exception,
            final HttpServletRequest request) {
        log.error("[SYSTEM_ERROR] Exception Type: {}, Request URI: {}, Message: {}",
                exception.getClass().getSimpleName(),
                request.getRequestURI(),
                exception.getMessage(),
                exception);

        CommonErrorCode systemErrorCode = CommonErrorCode.SYSTEM_ERROR;
        return ApiErrorResponse.toResponseEntity(systemErrorCode, systemErrorCode.getMessage());
    }
}
