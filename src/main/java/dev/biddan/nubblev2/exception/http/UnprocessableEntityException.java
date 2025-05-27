package dev.biddan.nubblev2.exception.http;

import dev.biddan.nubblev2.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

/**
 * 422 Unprocessable Entity - 비즈니스 규칙 위반
 */
public class UnprocessableEntityException extends BaseException {

    public UnprocessableEntityException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    @Override
    public String getTitle() {
        return "비즈니스 규칙 위반";
    }

    @Override
    protected void addProperties(ProblemDetail problemDetail) {
    }
}
