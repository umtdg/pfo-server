package com.umtdg.pfo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

class TestNotFoundException {
    @Test
    void givenEntityAndId_shouldConstructNotFoundException() {
        String expectedMessage = "Could not find 'Entity' with id 'EntityID'";
        HttpStatusCode expectedStatus = HttpStatus.NOT_FOUND;

        NotFoundException exception = new NotFoundException("Entity", "EntityID");

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals(
            ProblemDetail.forStatusAndDetail(expectedStatus, expectedMessage),
            exception.getBody()
        );
    }

    @Test
    void givenEntityAndListOfIds_shouldConstructNotFoundException() {
        List<String> ids = List.of("Id1", "Id2", "Id3");
        String expectedMessage = "Could not find 'Entity' with ids Id1, Id2, Id3";
        HttpStatusCode expectedStatus = HttpStatus.NOT_FOUND;

        NotFoundException exception = new NotFoundException("Entity", ids);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedStatus, exception.getStatusCode());
        assertEquals(
            ProblemDetail.forStatusAndDetail(expectedStatus, expectedMessage),
            exception.getBody()
        );
    }
}
