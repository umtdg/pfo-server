package com.umtdg.pfo.exception;

import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(
        {NotFoundException.class, SortByValidationException.class,
            DataIntegrityViolationException.class,}
    )
    public final ResponseEntity<Object> handleCustomExceptions(
        Exception ex, WebRequest request
    ) throws UnreachableException {
        switch (ex) {
            case NotFoundException subEx -> {
                return this
                    .handleNotFound(
                        subEx,
                        subEx.getHeaders(),
                        subEx.getStatusCode(),
                        request
                    );
            }
            case SortByValidationException subEx -> {
                return this
                    .handleSortByValidation(
                        subEx,
                        subEx.getHeaders(),
                        subEx.getStatusCode(),
                        request
                    );
            }
            default -> {
                HttpHeaders headers = new HttpHeaders();
                switch (ex) {
                    case DataIntegrityViolationException subEx -> {
                        return this
                            .handleDataIntegrityViolation(
                                subEx,
                                headers,
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                request
                            );
                    }
                    default -> throw new UnreachableException("GlobalExceptionHandler unhandled exception");
                }
            }
        }
    }

    protected ResponseEntity<Object> handleNotFound(
        NotFoundException ex, HttpHeaders headers, HttpStatusCode status,
        WebRequest request
    ) {
        return this
            .handleExceptionInternal(ex, (Object) null, headers, status, request);
    }

    protected ResponseEntity<Object> handleSortByValidation(
        SortByValidationException ex, HttpHeaders headers, HttpStatusCode status,
        WebRequest request
    ) {
        return this
            .handleExceptionInternal(ex, (Object) null, headers, status, request);
    }

    protected ResponseEntity<Object> handleDataIntegrityViolation(
        DataIntegrityViolationException ex, HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return this
            .handleExceptionInternal(ex, (Object) null, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
        WebRequest request
    ) {
        String detail = String
            .format(
                "Invalid value for fields: [%s]",
                ex
                    .getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> String.format("%s", error.getField()))
                    .collect(Collectors.joining(", "))
            );

        ProblemDetail body = ((ErrorResponse) ex)
            .updateAndGetBody(this.getMessageSource(), LocaleContextHolder.getLocale());
        body.setDetail(detail);

        return this
            .handleExceptionInternal(ex, (Object) body, headers, status, request);
    }
}
