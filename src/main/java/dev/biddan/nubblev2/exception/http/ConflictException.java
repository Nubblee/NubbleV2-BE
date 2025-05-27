package dev.biddan.nubblev2.exception.http;

import dev.biddan.nubblev2.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ConflictException extends BaseException {

    private final String conflictField;
    private final String conflictValue;

    public ConflictException(String message) {
        this(message, null, null);
    }

    public ConflictException(String message, String conflictField, String conflictValue) {
        super(message);
        this.conflictField = conflictField;
        this.conflictValue = conflictValue;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getTitle() {
        return "리소스 충돌";
    }

    @Override
    protected void addProperties(ProblemDetail problemDetail) {
        if (conflictField != null) {
            problemDetail.setProperty("conflictField", conflictField);
        }
        if (conflictValue != null) {
            problemDetail.setProperty("conflictValue", conflictValue);
        }
    }
}
