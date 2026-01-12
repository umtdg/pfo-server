package com.umtdg.pfo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

class TestUpdateFundStatsException {
    @Test
    void givenMessage_shouldConstructUpdateFundStatsException() {
        String expectedMessage = "Error when updating fund statistics";
        HttpStatusCode expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        UpdateFundsException exception = new UpdateFundsException(
            expectedMessage
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals(
            ProblemDetail.forStatusAndDetail(expectedStatus, expectedMessage),
            exception.getBody()
        );
    }

    @Test
    void givenMessageAndStatus_shouldConstructUpdateFundStatsException() {
        String expectedMessage = "Error when updating fund statistics";
        HttpStatusCode expectedStatus = HttpStatus.BAD_REQUEST;

        UpdateFundsException exception = new UpdateFundsException(
            expectedMessage, expectedStatus
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals(
            ProblemDetail.forStatusAndDetail(expectedStatus, expectedMessage),
            exception.getBody()
        );
    }
}
