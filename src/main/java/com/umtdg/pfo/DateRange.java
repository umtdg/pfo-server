package com.umtdg.pfo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DateRange)) {
            return false;
        }

        DateRange other = (DateRange) obj;
        return Objects.equals(this.start, other.start)
            && Objects.equals(this.end, other.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.start, this.end);
    }

    public List<DateRange> split() {
        LocalDate splitStart = this.start;
        LocalDate splitEnd = this.end;

        if (splitStart == null) {
            splitStart = splitEnd;
        }

        List<DateRange> ranges = new ArrayList<>();
        long months = ChronoUnit.MONTHS.between(splitStart, splitEnd);

        while (months >= 3) {
            LocalDate next = splitStart.plusMonths(3);
            if (next.isAfter(splitEnd)) {
                next = splitEnd;
            }
            ranges.add(new DateRange(splitStart, next));

            months -= 3;
            splitStart = next.plusDays(1);
        }

        if (!splitStart.isAfter(splitEnd)) {
            ranges.add(new DateRange(splitStart, splitEnd));
        }

        return ranges;
    }
}
