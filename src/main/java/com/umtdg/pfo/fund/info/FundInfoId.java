package com.umtdg.pfo.fund.info;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class FundInfoId implements Serializable {
    private String code;

    private LocalDate date;

    public FundInfoId() {
    }

    public FundInfoId(String code, LocalDate date) {
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

        if (!(obj instanceof FundInfoId))
            return false;

        FundInfoId other = (FundInfoId) obj;
        return Objects.equals(code, other.code)
            && Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, date);
    }
}
