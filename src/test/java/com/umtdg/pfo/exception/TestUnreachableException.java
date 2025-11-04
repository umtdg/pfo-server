package com.umtdg.pfo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

class TestUnreachableException {
    @Test
    void shouldConstructUnreachableException() {
        String expectedMessage = "Execution reached a place where it shouldn't: Some context to specify where";
        HttpStatusCode expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        UnreachableException exception = new UnreachableException(
            "Some context to specify where"
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals(
            ProblemDetail.forStatusAndDetail(expectedStatus, expectedMessage),
            exception.getBody()
        );
    }
}
