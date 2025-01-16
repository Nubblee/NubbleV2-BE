package dev.biddan.nubblev2.user.error;

import dev.biddan.nubblev2.common.error.ApiErrorResponse;
import dev.biddan.nubblev2.user.error.exception.UserLoginIdAlreadyExistsException;
import dev.biddan.nubblev2.user.error.exception.UserNicknameAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("dev.biddan.nubblev2.user")
public class UserExceptionHandler {

    @ExceptionHandler(UserLoginIdAlreadyExistsException.class)
    ResponseEntity<ApiErrorResponse> handleUserLoginIdAlreadyExistsException(
            final UserLoginIdAlreadyExistsException exception) {
        return ApiErrorResponse.toResponseEntity(
                UserErrorCode.LOGIN_ID_ALREADY_EXISTS, exception.getMessage());
    }

    @ExceptionHandler(UserNicknameAlreadyExistsException.class)
    ResponseEntity<ApiErrorResponse> handleUserNicknameAlreadyExistsException(
            final UserNicknameAlreadyExistsException exception) {
        return ApiErrorResponse.toResponseEntity(
                UserErrorCode.NICKNAME_ALREADY_EXISTS, exception.getMessage());
    }
}
