package com.umtdg.pfo.tefas;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TefasFundListRequest {
    @JsonProperty("dil")
    private String language = "TR";

    @JsonProperty("aramaMetni")
    private String query = null;

    @JsonProperty("basSira")
    private Integer startIndex = 1;

    @JsonProperty("bitSira")
    private Integer endIndex = 500;

    @JsonProperty("basTarih")
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate startDate;

    @JsonProperty("bitTarih")
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate endDate;

    @JsonProperty("fonGrubu")
    private String fundGroup = null;

    @JsonProperty("fonKodu")
    private String fundCode = null;

    @JsonProperty("fonTipi")
    private String fundType = "YAT";

    @JsonProperty("fonTurAciklama")
    private String fundTypeDesc = null;

    @JsonProperty("fonTurKod")
    private String fundTypeCode = null;

    @JsonProperty("kurucuKod")
    private String founderCode = null;

    @JsonProperty("sfonTurKod")
    private String sfundTypeCode = null;

    @JsonProperty("sira")
    private String sortBy = null;

    @JsonProperty("yon")
    private String sortDirection = null;

    public TefasFundListRequest(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }
}
