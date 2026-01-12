package com.umtdg.pfo.exception;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class TestGlobalExceptionHandler {
    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private WebRequest request;

    @Test
    void givenNotFoundException_shouldReturnProblemDetail() {
        NotFoundException ex = new NotFoundException("Entity", "Id");
        HttpStatusCode status = ex.getStatusCode();
        int statusValue = status.value();

        ResponseEntity<Object> response = assertDoesNotThrow(
            () -> handler.handleCustomExceptions(ex, request)
        );

        assertEquals(status, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());

        ProblemDetail body = (ProblemDetail) (response.getBody());

        assertEquals(ex.getMessage(), body.getDetail());
        assertEquals(statusValue, body.getStatus());
        assertNull(body.getInstance());
        assertNull(body.getProperties());
        assertEquals(
            HttpStatus.resolve(statusValue).getReasonPhrase(),
            body.getTitle()
        );
        assertEquals("about:blank", body.getType().toString());
    }

    @Test
    void givenSortByValidationException_shouldReturnProblemDetail() {
        SortByValidationException ex = new SortByValidationException(
            List.of("Arg1", "Arg2")
        );
        HttpStatusCode status = ex.getStatusCode();
        int statusValue = status.value();

        ResponseEntity<Object> response = assertDoesNotThrow(
            () -> handler.handleCustomExceptions(ex, request)
        );

        assertEquals(status, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());

        ProblemDetail body = (ProblemDetail) (response.getBody());

        assertEquals(ex.getMessage(), body.getDetail());
        assertEquals(statusValue, body.getStatus());
        assertNull(body.getInstance());
        assertNull(body.getProperties());
        assertEquals(
            HttpStatus.resolve(statusValue).getReasonPhrase(),
            body.getTitle()
        );
        assertEquals("about:blank", body.getType().toString());
    }

    @Test
    void givenUpdateFundStatsException_shouldReturnProblemDetail() {
        UpdateFundsException ex = new UpdateFundsException(
            "Error when updating fund statistics"
        );
        HttpStatusCode status = ex.getStatusCode();
        int statusValue = status.value();

        ResponseEntity<Object> response = assertDoesNotThrow(
            () -> handler.handleCustomExceptions(ex, request)
        );

        assertEquals(status, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());

        ProblemDetail body = (ProblemDetail) (response.getBody());

        assertEquals(ex.getMessage(), body.getDetail());
        assertEquals(statusValue, body.getStatus());
        assertNull(body.getInstance());
        assertNull(body.getProperties());
        assertEquals(
            HttpStatus.resolve(statusValue).getReasonPhrase(),
            body.getTitle()
        );
        assertEquals("about:blank", body.getType().toString());
    }

    @Test
    void givenDataIntegrityViolationException_shouldReturnProblemDetail() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
            "Data integrity violation"
        );
        HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
        int statusValue = status.value();

        ResponseEntity<Object> response = assertDoesNotThrow(
            () -> handler.handleCustomExceptions(ex, request)
        );

        assertEquals(status, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());

        ProblemDetail body = (ProblemDetail) (response.getBody());

        assertEquals(ex.getMessage(), body.getDetail());
        assertEquals(statusValue, body.getStatus());
        assertNull(body.getInstance());
        assertNull(body.getProperties());
        assertEquals(
            HttpStatus.resolve(statusValue).getReasonPhrase(),
            body.getTitle()
        );
        assertEquals("about:blank", body.getType().toString());
    }

    @Test
    void givenUnhandledException_shouldThrowUnreachableException() {
        Exception ex = new Exception("Unhandled exception");
        assertThrowsExactly(
            UnreachableException.class,
            () -> handler.handleCustomExceptions(ex, request)
        );
    }

    @Test
    void givenMethodArgumentNotValidException_shouldReturnProblemDetailWithFields() {
        MethodArgumentNotValidException ex = Mockito
            .mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);

        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        int statusValue = status.value();
        ProblemDetail expectedBody = ProblemDetail
            .forStatusAndDetail(status, "Invalid request content.");

        List<FieldError> fieldErrors = List
            .of(
                new FieldError("object1", "field1", "Invalid field1 in object1"),
                new FieldError("object1", "field2", "Invalid field2 in object1"),
                new FieldError("object2", "field1", "Invalid field1 in object2")
            );

        Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        Mockito.when(ex.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        Mockito
            .when(ex.updateAndGetBody(null, LocaleContextHolder.getLocale()))
            .thenReturn(expectedBody);

        ResponseEntity<Object> response = assertDoesNotThrow(
            () -> handler.handleException(ex, request)
        );

        assertEquals(status, response.getStatusCode());
        assertInstanceOf(ProblemDetail.class, response.getBody());

        ProblemDetail body = (ProblemDetail) (response.getBody());

        assertEquals(
            "Invalid value for fields: [field1, field2, field1]",
            body.getDetail()
        );
        assertEquals(statusValue, body.getStatus());
        assertNull(body.getInstance());
        assertNull(body.getProperties());
        assertEquals(
            HttpStatus.resolve(statusValue).getReasonPhrase(),
            body.getTitle()
        );
        assertEquals("about:blank", body.getType().toString());
    }
}
