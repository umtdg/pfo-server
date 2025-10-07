package com.umtdg.pfo;

import java.time.LocalDate;
import java.util.Objects;

public class DateRange {
    private LocalDate start = null;
    private LocalDate end = null;

    public DateRange() {
    }

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
        return Objects.equals(start, other.start)
            && Objects.equals(end, other.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
