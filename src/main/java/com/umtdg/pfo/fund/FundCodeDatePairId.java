package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.Objects;

public class FundCodeDatePairId {
    private String code;

    private LocalDate date;

    public FundCodeDatePairId() {
    }

    public FundCodeDatePairId(String code, LocalDate date) {
        this.code = code;
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof FundCodeDatePairId))
            return false;

        FundCodeDatePairId other = (FundCodeDatePairId) obj;
        return Objects.equals(code, other.code)
            && Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, date);
    }
}
