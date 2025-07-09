package com.umtdg.pfo;

import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    private static String idsToString(Iterable<String> ids) {
        String res = "[";

        Iterator<String> iter = ids.iterator();
        boolean hasNext = iter.hasNext();
        while (hasNext) {
            res += iter.next();
            if (hasNext) {
                res += ", ";
            }

            hasNext = iter.hasNext();
        }

        res += "]";
        return res;
    }

    public NotFoundException(String entity, String id) {
        super(String.format("Could not find '%s' with id '%s'", entity, id));
    }

    public NotFoundException(String entity, Iterable<String> ids) {
        super(String.format(
                "Could not find '%s' with ids %s",
                entity, idsToString(ids)));
    }
}
