package com.umtdg.pfo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

class TestTefasSessionCreationException {
    @Test
    void shouldConstructTefasSessionCreationException() {
        String expectedMessage = "Initial get request to Tefas for session creation failed";
        HttpStatusCode expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        TefasSessionCreationException exception = new TefasSessionCreationException();

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals(
            ProblemDetail.forStatusAndDetail(expectedStatus, expectedMessage),
            exception.getBody()
        );
    }
}
