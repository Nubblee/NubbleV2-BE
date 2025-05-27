package dev.biddan.nubblev2.exception.validation;

import dev.biddan.nubblev2.exception.validation.ValidationException.ValidationFieldError;
import java.net.URI;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 유효성 검증 실패
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<ValidationFieldError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationFieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ValidationException validationException = new ValidationException(validationErrors);
        ProblemDetail problemDetail = validationException.toProblemDetail(getRequestUri(request));

        return ResponseEntity.status(status)
                .headers(headers)
                .body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Class<?> requiredType = ex.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "알 수 없는 타입";

        String message = String.format(
                "요청 파라미터 '%s'의 값 '%s'이(가) '%s' 타입으로 변환될 수 없습니다.",
                ex.getName(), ex.getValue(), typeName);

        List<ValidationFieldError> validationErrors = List.of(new ValidationFieldError(ex.getName(), message));
        ValidationException validationException = new ValidationException(validationErrors);

        ProblemDetail problemDetail = validationException.toProblemDetail(getRequestUri(request));

        return ResponseEntity.badRequest().body(problemDetail);
    }

    private URI getRequestUri(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return URI.create(servletRequest.getRequest().getRequestURI());
        }
        return URI.create("/");
    }
}
