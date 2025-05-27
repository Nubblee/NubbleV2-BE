package dev.biddan.nubblev2.exception.http;

import dev.biddan.nubblev2.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class NotFoundException extends BaseException {

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getTitle() {
        return "리소스를 찾을 수 없음";
    }

    @Override
    protected void addProperties(ProblemDetail problemDetail) {
    }
}
