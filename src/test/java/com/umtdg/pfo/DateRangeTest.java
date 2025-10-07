package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DateRangeTest {
    @Test
    void givenStartEnd_thenConstructRange() {
        LocalDate start = LocalDate.of(2025, 9, 7);
        LocalDate end = LocalDate.of(2025, 10, 7);

        DateRange range = new DateRange(start, end);

        assertEquals(start, range.getStart());
        assertEquals(end, range.getEnd());
        assertEquals("[2025-09-07 - 2025-10-07]", range.toString());
    }

    @Test
    void givenTwoRanges_whenEqual_thenReturnTrueAndSameHash() {
        DateRange range = new DateRange(
            LocalDate.now(), LocalDate.now().plusMonths(3).plusDays(3)
        );
        DateRange range2 = new DateRange(range.getStart(), range.getEnd());

        assertEquals(range, range2);
        assertEquals(range.hashCode(), range2.hashCode());
    }

    @Test
    void givenTwoRanges_whenDifferent_thenReturnFalseAndDifferentHash() {
        DateRange range = new DateRange(
            LocalDate.now(), LocalDate.now().plusMonths(3).plusDays(3)
        );
        DateRange range2 = new DateRange(range.getStart().plusDays(1), range.getEnd());

        assertNotEquals(range, range2);
        assertNotEquals(range.hashCode(), range2.hashCode());
    }
}
