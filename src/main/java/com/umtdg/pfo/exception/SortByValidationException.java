package com.umtdg.pfo.exception;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import jakarta.servlet.ServletException;

public class SortByValidationException extends ServletException
    implements
        ErrorResponse {
    private final ProblemDetail body;

    public SortByValidationException(Collection<String> invalidParameters) {
        super(String.format("Invalid sort by parameters %s", invalidParameters));

        this.body = ProblemDetail
            .forStatusAndDetail(this.getStatusCode(), this.getMessage());
    }

    @Override
    public ProblemDetail getBody() {
        return this.body;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
