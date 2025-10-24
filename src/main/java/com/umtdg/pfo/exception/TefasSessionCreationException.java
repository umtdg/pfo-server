package com.umtdg.pfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import jakarta.servlet.ServletException;

public class TefasSessionCreationException extends ServletException implements ErrorResponse {
    private final ProblemDetail body;

    public TefasSessionCreationException() {
        super("Initial get request to Tefas for session creation failed");

        body = ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public ProblemDetail getBody() {
        return body;
    }

}
