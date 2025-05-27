package dev.biddan.nubblev2.exception.validation;

import dev.biddan.nubblev2.exception.http.BadRequestException;
import java.util.List;
import org.springframework.http.ProblemDetail;

/**
 * 유효성 검증 오류를 포함하는 예외 (400 Bad Request)
 */
public class ValidationException extends BadRequestException {

    private final List<ValidationFieldError> errors;

    public ValidationException(List<ValidationFieldError> errors) {
        super("요청에 포함된 데이터가 유효성 검증에 실패했습니다");
        this.errors = errors;
    }

    public ValidationException(String message, List<ValidationFieldError> errors) {
        super(message);
        this.errors = errors;
    }

    @Override
    public String getTitle() {
        return "입력값 유효성 검증 실패";
    }

    @Override
    protected void addProperties(ProblemDetail problemDetail) {
        problemDetail.setProperty("errors", errors);
    }

    public record ValidationFieldError(String field, String message) {
    }
}
