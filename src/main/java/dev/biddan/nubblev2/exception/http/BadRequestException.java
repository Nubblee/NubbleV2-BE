package dev.biddan.nubblev2.exception.http;

import dev.biddan.nubblev2.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getTitle() {
        return "유효하지 않은 요청";
    }

    @Override
    protected void addProperties(ProblemDetail problemDetail) {
    }
}
