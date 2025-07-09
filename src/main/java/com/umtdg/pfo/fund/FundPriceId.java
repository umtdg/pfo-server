package com.umtdg.pfo.fund;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class FundPriceId implements Serializable {
    private String code;

    private LocalDate date;

    public FundPriceId() {
    }

    public FundPriceId(String code, LocalDate date) {
        this.code = code;
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof FundPriceId))
            return false;

        FundPriceId other = (FundPriceId) obj;
        return Objects.equals(code, other.code)
            && Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, date);
    }
}
