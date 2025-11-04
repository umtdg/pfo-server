package com.umtdg.pfo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

class TestSortByValidationException {
    @Test
    void givenParamList_shouldConstructSortByValidationException() {
        String expectedMessage = "Invalid sort by parameters [Param1, Param2, Param3]";
        HttpStatusCode expectedStatus = HttpStatus.BAD_REQUEST;
        List<String> invalidParameters = List.of("Param1", "Param2", "Param3");

        SortByValidationException exception = new SortByValidationException(
            invalidParameters
        );

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals(
            ProblemDetail.forStatusAndDetail(expectedStatus, expectedMessage),
            exception.getBody()
        );
    }
}
