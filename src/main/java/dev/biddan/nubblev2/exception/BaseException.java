package dev.biddan.nubblev2.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public abstract class BaseException extends RuntimeException {

    public ProblemDetail toProblemDetail(URI instance) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(getStatus());
        problemDetail.setType(getType());
        problemDetail.setTitle(getTitle());
        problemDetail.setDetail(getDetail());
        problemDetail.setInstance(instance);

        addProperties(problemDetail);

        return problemDetail;
    }

    public URI getType() {
        return URI.create("about:blank");
    }

    public String getDetail() {
        return getMessage();
    }

    public abstract HttpStatus getStatus();

    public abstract String getTitle();

    protected abstract void addProperties(ProblemDetail problemDetail);

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
