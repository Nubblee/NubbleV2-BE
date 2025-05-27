package dev.biddan.nubblev2.exception;

import dev.biddan.nubblev2.exception.http.BadRequestException;
import dev.biddan.nubblev2.exception.http.InternalServerErrorException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 애플리케이션 예외
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(BaseException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ex.toProblemDetail(URI.create(request.getRequestURI()));

        return ResponseEntity.status(ex.getStatus())
                .body(problemDetail);
    }

    /**
     * 미처리 예외
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleUncaughtException(Throwable ex, WebRequest request) {
        log.error("처리되지 않은 예외 발생: ", ex);

        InternalServerErrorException internalEx = new InternalServerErrorException(
                "요청을 처리하는 중 예기치 않은 오류가 발생했습니다", ex);
        ProblemDetail problemDetail = internalEx.toProblemDetail(getRequestUri(request));

        return ResponseEntity.internalServerError().body(problemDetail);
    }

    /**
     * IllegalArgumentException - 잘못된 인자 전달
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.warn("잘못된 인자 전달: {}", ex.getMessage());

        BadRequestException badRequestEx = new BadRequestException(ex.getMessage(), ex);
        ProblemDetail problemDetail = badRequestEx.toProblemDetail(getRequestUri(request));

        return ResponseEntity.badRequest().body(problemDetail);
    }

    private URI getRequestUri(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return URI.create(servletRequest.getRequest().getRequestURI());
        }
        return URI.create("/");
    }
}
