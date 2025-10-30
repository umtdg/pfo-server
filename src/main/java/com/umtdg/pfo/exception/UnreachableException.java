package com.umtdg.pfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import jakarta.servlet.ServletException;

public class UnreachableException extends ServletException implements ErrorResponse {
    private final ProblemDetail body;

    public UnreachableException(String context) {
        super(
            String.format("Execution reached a place where it shouldn't: %s", context)
        );

        this.body = ProblemDetail
            .forStatusAndDetail(this.getStatusCode(), this.getMessage());
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public ProblemDetail getBody() {
        return this.body;
    }
}
