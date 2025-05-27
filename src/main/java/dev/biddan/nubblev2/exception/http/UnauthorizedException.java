package dev.biddan.nubblev2.exception.http;

import dev.biddan.nubblev2.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super("이 리소스에 접근하려면 로그인이 필요합니다");
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getTitle() {
        return "인증 필요";
    }

    @Override
    protected void addProperties(ProblemDetail problemDetail) {
    }
}
