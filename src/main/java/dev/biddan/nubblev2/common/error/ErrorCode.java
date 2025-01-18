package dev.biddan.nubblev2.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getHttpStatus();

    String getMessage();

    String getName();
}
