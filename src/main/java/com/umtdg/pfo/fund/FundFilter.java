package com.umtdg.pfo.fund;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class FundFilter {
    private List<String> codes;

    @DateTimeFormat(pattern = "MM.dd.yyyy")
    private LocalDate date;

    // TODO: Sort by
    // TODO: Sort asc/desc

    public FundFilter() {
        codes = new ArrayList<>();
        date = null;
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

    public void setDate(LocalDate start) {
        this.date = start;
    }
}
