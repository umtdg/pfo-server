package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FundFilter {
    @NotNull
    private List<@Size(
        min = 3, max = 3, message = "Fund codes must have length 3"
    ) String> codes;

    @DateTimeFormat(pattern = "MM.dd.yyyy")
    private LocalDate date;

    @DateTimeFormat(pattern = "MM.dd.yyyy")
    private LocalDate fetchFrom;

    public FundFilter() {
        codes = List.of();
        date = null;
        fetchFrom = null;
    }

    public static FundFilter ofCodes(List<String> codes) {
        FundFilter filter = new FundFilter();
        filter.codes.addAll(codes);

        return filter;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getFetchFrom() {
        return fetchFrom;
    }

    public void setFetchFrom(LocalDate fetchFrom) {
        this.fetchFrom = fetchFrom;
    }

    @Override
    public String toString() {
        return codes + " at [" + fetchFrom + " - " + date + "]";
    }
}
