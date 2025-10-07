package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class DateUtilsTest {
    @Test
    void givenDate_thenGetPreviousBusinessDay() {
        LocalDate date = LocalDate.of(2025, 10, 7); // Tue, 07 Oct 2025

        LocalDate prevBDay = DateUtils.prevBDay(date);

        assertEquals(date, prevBDay);
    }

    @Test
    void givenDateTime_thenGetPreviousBusinessDay() {
        LocalDate sunday = LocalDate.of(2025, 10, 5); // Sun, 05 Oct 2025
        LocalDate tuesday = LocalDate.of(2025, 10, 7); // Tue, 07 Oct 2025

        LocalDate sunPrevBDay = LocalDate.of(2025, 10, 3); // Fri, 03 Oct 2025
        // Mon, 06 Oct 2025
        LocalDate tuePrevBDayBefore18 = LocalDate.of(2025, 10, 6);

        // Sun, 05 Oct 2025 - 17:33 (05:33 PM)
        LocalDateTime sunBefore18 = LocalDateTime.of(sunday, LocalTime.of(17, 33));

        // Sun, 05 Oct 2025 - 19:27 (07:27 PM)
        LocalDateTime sunAfter18 = LocalDateTime.of(sunday, LocalTime.of(19, 27));

        // Tue, 07 Oct 2025 - 12:47 (12:47 PM)
        LocalDateTime tueBefore18 = LocalDateTime.of(tuesday, LocalTime.of(12, 47));

        // Tue, 07 Oct 2025 - 21:03 (09:03 PM)
        LocalDateTime tueAfter18 = LocalDateTime.of(tuesday, LocalTime.of(21, 3));

        assertEquals(sunPrevBDay, DateUtils.prevBDay(sunBefore18));
        assertEquals(sunPrevBDay, DateUtils.prevBDay(sunAfter18));
        assertEquals(tuePrevBDayBefore18, DateUtils.prevBDay(tueBefore18));
        assertEquals(tuesday, DateUtils.prevBDay(tueAfter18));
    }

    @Test
    void givenValidRange_thenSplitRange() {
        LocalDate start = LocalDate.of(2021, 3, 18);

        DateRange zeroDay = new DateRange(start, start);
        List<DateRange> zeroDayRanges = List.of(zeroDay);
        assertIterableEquals(zeroDayRanges, DateUtils.splitDateRange(zeroDay));

        DateRange oneDay = new DateRange(start, start.plusDays(1));
        List<DateRange> oneDayRanges = List.of(oneDay);
        assertIterableEquals(oneDayRanges, DateUtils.splitDateRange(oneDay));

        DateRange shorterThan3Months = new DateRange(
            start, start.plusMonths(1).plusDays(25)
        );
        List<DateRange> shorterThan3MonthsRanges = List.of(shorterThan3Months);
        assertIterableEquals(
            shorterThan3MonthsRanges,
            DateUtils.splitDateRange(shorterThan3Months)
        );

        DateRange exactly3Months = new DateRange(start, start.plusMonths(3));
        List<DateRange> exactly3MonthsRanges = List.of(exactly3Months);
        assertIterableEquals(
            exactly3MonthsRanges,
            DateUtils.splitDateRange(exactly3Months)
        );

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
        assertIterableEquals(
            exactly3MonthsPlus1Ranges,
            DateUtils.splitDateRange(exactly3MonthsPlus1)
        );

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
        assertIterableEquals(
            exactly3MonthsPlus2Ranges,
            DateUtils.splitDateRange(exactly3MonthsPlus2)
        );

        DateRange exactMultipleOf3Months = new DateRange(start, start.plusMonths(9));
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
            DateUtils.splitDateRange(exactMultipleOf3Months)
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
        assertIterableEquals(
            longerThan3MonthsRanges,
            DateUtils.splitDateRange(longerThan3Months)
        );
    }

    @Test
    void givenRangeWithoutStart_thenSplitRange() {
    }

    @Test
    void givenRangeWithEndAfterStart_thenThrow() {
    }

    @Test
    void givenRangeWithStartAndEndNull_thenThrow() {
    }
}
