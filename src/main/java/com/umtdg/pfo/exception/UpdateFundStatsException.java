package com.umtdg.pfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import jakarta.servlet.ServletException;

public class UpdateFundStatsException extends ServletException
    implements
        ErrorResponse {
    private final ProblemDetail body;
    private final HttpStatusCode statusCode;

    public UpdateFundStatsException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public UpdateFundStatsException(String message, HttpStatusCode statusCode) {
        super(message);

        this.statusCode = statusCode;
        this.body = ProblemDetail
            .forStatusAndDetail(this.statusCode, this.getMessage());
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return this.statusCode;
    }

    @Override
    public ProblemDetail getBody() {
        return this.body;
    }

}
