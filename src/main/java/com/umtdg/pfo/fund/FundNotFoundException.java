package com.umtdg.pfo.fund;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FundNotFoundException extends RuntimeException {
    public FundNotFoundException(String code) {
        super(String.format("Could not find fund '%s'", code));
    }
}
