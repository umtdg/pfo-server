package com.umtdg.pfo;

import java.time.LocalDate;

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
}
