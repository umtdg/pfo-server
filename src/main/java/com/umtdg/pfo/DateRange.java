package com.umtdg.pfo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DateRange {
    private LocalDate start = null;
    private LocalDate end = null;

    public DateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "[" + start + " - " + end + "]";
    }

    public List<DateRange> split() {
        if (this.start == null) {
            this.start = this.end;
        }

        List<DateRange> ranges = new ArrayList<>();
        long months = ChronoUnit.MONTHS.between(this.start, this.end);

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
}
