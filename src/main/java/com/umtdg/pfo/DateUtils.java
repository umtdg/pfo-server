package com.umtdg.pfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtils {
    private DateUtils() {
    }

    public static LocalDate prevBDay() {
        return prevBDay(LocalDateTime.now());
    }

    public static LocalDate prevBDay(LocalDate base) {
        return prevBDay(LocalDateTime.of(base, LocalTime.of(18, 0)));
    }

    public static LocalDate prevBDay(LocalDateTime base) {
        if (base.getHour() < 18) {
            base = base.minusDays(1);
        }

        DayOfWeek dow = base.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY) {
            base = base.minusDays(1);
        } else if (dow == DayOfWeek.SUNDAY) {
            base = base.minusDays(2);
        }

        return base.toLocalDate();
    }
}
