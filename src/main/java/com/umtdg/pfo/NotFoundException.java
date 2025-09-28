package com.umtdg.pfo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, String id) {
        super(String.format("Could not find '%s' with id '%s'", entity, id));
    }

    public NotFoundException(String entity, Iterable<String> ids) {
        super(
            String
                .format(
                    "Could not find '%s' with ids %s",
                    entity,
                    String.join(", ", ids)
                )
        );
    }
}
