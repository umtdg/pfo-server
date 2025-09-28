package com.umtdg.pfo.fund;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.umtdg.pfo.NotFoundException;
import com.umtdg.pfo.SortParameters;

@RestControllerAdvice
public class FundControllerAdvice {
    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String fundNotFoundHandler(NotFoundException exc) {
        return exc.getMessage();
    }

    @ExceptionHandler({SortParameters.ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String sortParameterValidationExceptionHandler(
        SortParameters.ValidationException exc
    ) {
        return exc.getMessage();
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String dataIntegrityViolationHandler(DataIntegrityViolationException exc) {
        return exc.toString();
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exc
    ) {
        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getAllErrors().forEach(e -> {
            String field = ((FieldError) e).getField();
            String msg = e.getDefaultMessage();
            errors.put(field, msg);
        });

        return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
    }
}
