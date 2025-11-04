package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
}
