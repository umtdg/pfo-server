package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestDateRange {
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
    void givenStartEnd_thenConstructRangeAndSetStartEnd() {
        LocalDate start = LocalDate.of(2025, 9, 7);
        LocalDate end = LocalDate.of(2025, 10, 7);

        DateRange range = new DateRange(null, null);
        range.setStart(start);
        range.setEnd(end);

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
        DateRange range3 = range;

        assertEquals(range, range2);
        assertEquals(range.hashCode(), range2.hashCode());

        assertEquals(range, range3);
        assertEquals(range.hashCode(), range3.hashCode());
    }

    @Test
    void givenTwoRanges_whenDifferent_thenReturnFalseAndDifferentHash() {
        DateRange range = new DateRange(
            LocalDate.now(), LocalDate.now().plusMonths(3).plusDays(3)
        );
        DateRange range2 = new DateRange(range.getStart().plusDays(1), range.getEnd());
        DateRange range3 = new DateRange(range.getStart(), range.getEnd().plusDays(1));
        DateRange range4 = new DateRange(
            range.getStart().plusDays(1), range.getEnd().plusDays(1)
        );

        assertNotEquals(range, range2);
        assertNotEquals(range.hashCode(), range2.hashCode());

        assertNotEquals(range, range3);
        assertNotEquals(range.hashCode(), range3.hashCode());

        assertNotEquals(range, range4);
        assertNotEquals(range.hashCode(), range4.hashCode());
    }

    @Test
    void givenRangeAndObject_thenNotEqual() {
        DateRange range = new DateRange(
            LocalDate.now(), LocalDate.now().plusMonths(3).plusDays(3)
        );
        String obj = "obj";

        assertNotEquals(range, obj);
    }

    @Test
    void givenValidRange_thenSplitRange() {
        LocalDate start = LocalDate.of(2021, 3, 18);

        DateRange zeroDay = new DateRange(start, start);
        List<DateRange> zeroDayRanges = List.of(zeroDay);
        assertIterableEquals(zeroDayRanges, zeroDay.split());

        DateRange oneDay = new DateRange(start, start.plusDays(1));
        List<DateRange> oneDayRanges = List.of(oneDay);
        assertIterableEquals(oneDayRanges, oneDay.split());

        DateRange shorterThan3Months = new DateRange(
            start, start.plusMonths(1).plusDays(25)
        );
        List<DateRange> shorterThan3MonthsRanges = List.of(shorterThan3Months);
        assertIterableEquals(shorterThan3MonthsRanges, shorterThan3Months.split());

        DateRange exactly3Months = new DateRange(start, start.plusMonths(3));
        List<DateRange> exactly3MonthsRanges = List.of(exactly3Months);
        assertIterableEquals(exactly3MonthsRanges, exactly3Months.split());

        DateRange exactly3MonthsPlus1 = new DateRange(
            start, start.plusMonths(3).plusDays(1)
        );
        List<DateRange> exactly3MonthsPlus1Ranges = List
            .of(
                exactly3Months,
                new DateRange(
                    exactly3MonthsPlus1.getEnd(), exactly3MonthsPlus1.getEnd()
                )
            );
        assertIterableEquals(exactly3MonthsPlus1Ranges, exactly3MonthsPlus1.split());

        DateRange exactly3MonthsPlus2 = new DateRange(
            start, start.plusMonths(3).plusDays(2)
        );
        List<DateRange> exactly3MonthsPlus2Ranges = List
            .of(
                exactly3Months,
                new DateRange(
                    start.plusMonths(3).plusDays(1), exactly3MonthsPlus2.getEnd()
                )
            );
        assertIterableEquals(exactly3MonthsPlus2Ranges, exactly3MonthsPlus2.split());

        DateRange exactMultipleOf3Months = new DateRange(start, start.plusMonths(3 * 3));
        List<DateRange> exactMultipleOf3MonthsRanges = List
            .of(
                exactly3Months,
                new DateRange(
                    start.plusMonths(3).plusDays(1), start.plusMonths(6).plusDays(1)
                ),
                new DateRange(
                    start.plusMonths(6).plusDays(2), exactMultipleOf3Months.getEnd()
                )
            );
        assertIterableEquals(
            exactMultipleOf3MonthsRanges,
            exactMultipleOf3Months.split()
        );

        DateRange longerThan3Months = new DateRange(
            start, start.plusMonths(32).plusDays(7)
        );
        List<DateRange> longerThan3MonthsRanges = List
            .of(
                exactly3Months,
                new DateRange(
                    start.plusMonths(3).plusDays(1), start.plusMonths(6).plusDays(1)
                ),
                new DateRange(
                    start.plusMonths(6).plusDays(2), start.plusMonths(9).plusDays(2)
                ),
                new DateRange(
                    start.plusMonths(9).plusDays(3), start.plusMonths(12).plusDays(3)
                ),
                new DateRange(
                    start.plusMonths(12).plusDays(4), start.plusMonths(15).plusDays(4)
                ),
                new DateRange(
                    start.plusMonths(15).plusDays(5), start.plusMonths(18).plusDays(5)
                ),
                new DateRange(
                    start.plusMonths(18).plusDays(6), start.plusMonths(21).plusDays(6)
                ),
                new DateRange(
                    start.plusMonths(21).plusDays(7), start.plusMonths(24).plusDays(7)
                ),
                new DateRange(
                    start.plusMonths(24).plusDays(8), start.plusMonths(27).plusDays(8)
                ),
                new DateRange(
                    start.plusMonths(27).plusDays(9), start.plusMonths(30).plusDays(9)
                ),
                new DateRange(
                    start.plusMonths(30).plusDays(10), longerThan3Months.getEnd()
                )
            );
        assertIterableEquals(longerThan3MonthsRanges, longerThan3Months.split());
    }

    @Test
    void givenRangeWithoutStart_thenSplitRange() {
        LocalDate end = LocalDate.of(2025, 10, 8);
        DateRange range = new DateRange(null, end);
        List<DateRange> splitRanges = List.of(new DateRange(end, end));

        assertIterableEquals(splitRanges, range.split());
    }

    // TODO: Fix in DateRange and complete this test
    @Test
    void givenRangeWithEndAfterStart_thenThrow() {
    }

    // TODO: Fix in DateRange and complete this test
    @Test
    void givenRangeWithStartAndEndNull_thenThrow() {
    }
}
