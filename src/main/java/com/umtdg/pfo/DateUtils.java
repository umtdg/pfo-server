package com.umtdg.pfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.umtdg.pfo.fund.FundFilter;
import com.umtdg.pfo.fund.FundPriceRepository;

public class DateUtils {
    public static LocalDate prevBDay() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() < 18) {
            now = now.minusDays(1);
        }

        DayOfWeek dow = now.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY) {
            now = now.minusDays(1);
        } else if (dow == DayOfWeek.SUNDAY) {
            now = now.minusDays(2);
        }

        return now.toLocalDate();
    }

    public static List<DateRange> splitDateRange(DateRange origRange)
        throws IllegalArgumentException {
        LocalDate start = origRange.getStart();
        LocalDate end = origRange.getEnd();

        if (start == null) {
            start = end;
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                String
                    .format("Start date '{}' cannot be after end date '{}'", start, end)
            );
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
            LocalDate lastUpdated = priceRepository.findLatestDate();
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
