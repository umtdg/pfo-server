package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Size;

public class FundFilter {
    private Set<@Size(
        min = 3, max = 3, message = "Fund codes must have length 3"
    ) String> codes;

    @DateTimeFormat(pattern = "MM.dd.yyyy")
    private LocalDate date;

    public FundFilter() {
        codes = null;
        date = null;
    }

    public FundFilter(Collection<String> codes) {
        this.codes = new HashSet<>(codes);
        date = null;
    }

    public Set<String> getCodes() {
        return codes;
    }

    public void setCodes(Set<String> codes) {
        this.codes = codes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FundFilter {codes=" + codes + ", date=" + date + "}";
    }
}
