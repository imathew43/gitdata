package com.branchapp.gitdata.error;

import com.branchapp.gitdata.model.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
    private static final int NOT_FOUND_CODE = 404;
    private static final int TOO_MANY_REQUESTS_CODE = 429;
    private static final int UNEXPECTED_ERROR_CODE = 500;

    private static final String RETRY_AFTER_HEADER = "Retry-After";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleNotFound(UserNotFoundException e, HttpServletResponse response) {
        return new ErrorResponse(e.getMessage(), NOT_FOUND_CODE);
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(TooManyRequestsException.class)
    public ErrorResponse handleTooManyRequests(TooManyRequestsException e, HttpServletResponse response) {
        response.setHeader(RETRY_AFTER_HEADER, "" + e.getDelayInSeconds());
        return new ErrorResponse(e.getMessage(), TOO_MANY_REQUESTS_CODE);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(Exception e, WebRequest request) {
        log.error("Unexpected exception found: {}", e.getMessage(), e);
        return new ErrorResponse("Unexpected error occurred", UNEXPECTED_ERROR_CODE);
    }
}
