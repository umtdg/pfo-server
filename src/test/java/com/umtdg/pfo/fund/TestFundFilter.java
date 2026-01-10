package com.umtdg.pfo.fund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TestFundFilter {
    @Test
    void shouldDefaultConstructFundFilter() {
        FundFilter filter = new FundFilter();

        assertNull(filter.getCodes());
        assertNull(filter.getDate());
    }

    @Test
    void givenCodes_shouldConstructFundFilter() {
        FundFilter filter = new FundFilter(Set.of("FUN", "DUN", "FOO"));

        assertEquals(3, filter.getCodes().size());
        assertNull(filter.getDate());
    }

    @Test
    void givenFundFilter_shouldSetCodesDateAndFetchFrom() {
        List<String> initialCodes = List.of("FUN");

        LocalDate date = LocalDate.of(2025, 05, 25);
        Set<String> newCodes = new HashSet<>(2);
        newCodes.add("DUN");
        newCodes.add("FOO");

        FundFilter filter = new FundFilter(initialCodes);

        filter.setCodes(newCodes);
        assertIterableEquals(newCodes, filter.getCodes());

        filter.setDate(date);
        assertEquals(date, filter.getDate());
    }
}
