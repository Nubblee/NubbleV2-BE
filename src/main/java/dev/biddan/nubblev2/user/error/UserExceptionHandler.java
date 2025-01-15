package dev.biddan.nubblev2.user.error;

import dev.biddan.nubblev2.common.error.ApiErrorResponse;
import dev.biddan.nubblev2.user.error.exception.UserNicknameAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("dev.biddan.nubblev2.user")
public class UserExceptionHandler {

    @ExceptionHandler(UserNicknameAlreadyExistsException.class)
    ResponseEntity<ApiErrorResponse> handleUserNicknameAlreadyExistsException(
            final UserNicknameAlreadyExistsException exception) {
        return ApiErrorResponse.toResponseEntity(
                UserErrorCode.NICKNAME_ALREADY_EXISTS, exception.getMessage());
    }
}
