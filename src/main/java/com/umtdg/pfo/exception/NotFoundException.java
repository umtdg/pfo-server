package com.umtdg.pfo.exception;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import jakarta.servlet.ServletException;

public class NotFoundException extends ServletException implements ErrorResponse {
    private final ProblemDetail body;

    public NotFoundException(String entity, String id) {
        super(String.format("Could not find '%s' with id '%s'", entity, id));

        this.body = ProblemDetail
            .forStatusAndDetail(this.getStatusCode(), this.getMessage());
    }

    public NotFoundException(String entity, Collection<String> ids) {
        super(
            String
                .format(
                    "Could not find '%s' with ids %s",
                    entity,
                    String.join(", ", ids)
                )
        );

        this.body = ProblemDetail
            .forStatusAndDetail(this.getStatusCode(), this.getMessage());
    }

    @Override
    public ProblemDetail getBody() {
        return this.body;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }
}
