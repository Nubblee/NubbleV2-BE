package dev.biddan.nubblev2.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class InternalServerErrorException extends dev.biddan.nubblev2.exception.BaseException {

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getTitle() {
        return "서버 내부 오류";
    }

    @Override
    protected void addProperties(ProblemDetail problemDetail) {
    }
}
