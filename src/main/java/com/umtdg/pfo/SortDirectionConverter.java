package com.umtdg.pfo;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;

public class SortDirectionConverter implements Converter<String, Sort.Direction> {
    @Override
    public Sort.Direction convert(String arg0) {
        return Sort.Direction.fromString(arg0);
    }
}
