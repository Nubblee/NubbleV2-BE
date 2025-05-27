package dev.biddan.nubblev2.exception.http;

import dev.biddan.nubblev2.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ForbiddenException extends BaseException {

    public ForbiddenException() {
        super("이 작업을 수행할 권한이 없습니다");
    }

    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getTitle() {
        return "접근 권한 없음";
    }

    @Override
    protected void addProperties(ProblemDetail problemDetail) {
    }
}
