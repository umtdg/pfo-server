package com.umtdg.pfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.umtdg.pfo.fund.FundFilter;
import com.umtdg.pfo.fund.price.FundPriceRepository;

public class DateUtils {
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

    public static List<DateRange> splitDateRange(DateRange origRange)
        throws IllegalArgumentException {
        LocalDate start = origRange.getStart();
        LocalDate end = origRange.getEnd();

        if (start == null) {
            start = end;
        }

        List<DateRange> ranges = new ArrayList<>();
        long months = ChronoUnit.MONTHS.between(start, end);

        while (months >= 3) {
            LocalDate next = start.plusMonths(3);
            ranges.add(new DateRange(start, next));

            months -= 3;
            start = next.plusDays(1);
        }

        if (!start.isAfter(end)) {
            ranges.add(new DateRange(start, end));
        }

        return ranges;
    }

    public static FundFilter checkFundDateFilters(
        FundFilter filter, FundPriceRepository priceRepository
    ) {
        LocalDate date = filter != null ? filter.getDate() : null;
        if (date == null || date.isAfter(LocalDate.now())) {
            date = prevBDay();
        }

        LocalDate fetchFrom = filter != null ? filter.getFetchFrom() : null;
        if (fetchFrom == null) {
            LocalDate lastUpdated = priceRepository != null
                ? priceRepository.findTopDate()
                : null;
            if (lastUpdated == null) {
                lastUpdated = date.minusDays(1);
            }

            fetchFrom = lastUpdated;
        }

        if (filter == null) {
            filter = new FundFilter();
        }

        filter.setDate(date);
        filter.setFetchFrom(fetchFrom);
        return filter;
    }
}
