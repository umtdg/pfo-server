package com.umtdg.pfo.fund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestFundFilter {
    @Test
    void shouldDefaultConstructFundFilter() {
        FundFilter filter = new FundFilter();

        assertEquals(0, filter.getCodes().size());
        assertNull(filter.getDate());
        assertNull(filter.getFetchFrom());
    }

    @Test
    void givenCodes_shouldConstructFundFilter() {
        List<String> codes = List.of("FUN", "DUN", "FOO");
        FundFilter filter = FundFilter.ofCodes(codes);

        assertEquals(3, filter.getCodes().size());
        assertNull(filter.getDate());
        assertNull(filter.getFetchFrom());
    }

    @Test
    void givenFundFilter_shouldSetCodesDateAndFetchFrom() {
        List<String> initialCodes = List.of("FUN");

        LocalDate date = LocalDate.of(2025, 05, 25);
        LocalDate fetchFrom = LocalDate.of(2025, 05, 01);
        List<String> newCodes = new ArrayList<>();
        newCodes.add("DUN");
        newCodes.add("FOO");

        FundFilter filter = FundFilter.ofCodes(initialCodes);

        filter.setCodes(newCodes);
        assertIterableEquals(newCodes, filter.getCodes());

        filter.setDate(date);
        assertEquals(date, filter.getDate());

        filter.setFetchFrom(fetchFrom);
        assertEquals(fetchFrom, filter.getFetchFrom());
    }

    @Test
    void givenFundFilter_shouldConvertToString() {
        FundFilter filter = FundFilter.ofCodes(List.of("FUN", "DUN"));

        assertEquals("[FUN, DUN] at [null - null]", filter.toString());

        filter.setFetchFrom(LocalDate.of(2025, 04, 07));
        assertEquals("[FUN, DUN] at [2025-04-07 - null]", filter.toString());

        filter.setDate(LocalDate.of(2025, 04, 10));
        assertEquals("[FUN, DUN] at [2025-04-07 - 2025-04-10]", filter.toString());
    }
}
