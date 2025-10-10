package com.umtdg.pfo.tefas;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TefasFetchParams {
    @JsonProperty("fontip")
    private String fundType = "YAT";

    @JsonProperty("fonkod")
    private String code = "";

    @JsonProperty("bastarih")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate start;

    @JsonProperty("bittarih")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate end;

    public TefasFetchParams() {
    }

    public TefasFetchParams(
        LocalDate start, LocalDate end
    ) {
        this.start = start;
        this.end = end;
    }

    public TefasFetchParams(
        String fundType, String code, LocalDate start, LocalDate end
    ) {
        this.fundType = fundType;
        this.code = code;
        this.start = start;
        this.end = end;
    }
}
