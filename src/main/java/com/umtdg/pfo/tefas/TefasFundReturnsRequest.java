package com.umtdg.pfo.tefas;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TefasFundReturnsRequest {
    @JsonProperty("dil")
    private String language = "TR";

    @JsonProperty("fonTipi")
    private String fundType = "YAT";

    @JsonProperty("kurucuKodu")
    private String founderCode = null;

    @JsonProperty("sfonTurKod")
    private String sfundTypeCode = null;

    @JsonProperty("fonTurAciklama")
    private String fundTypeDesc = null;

    @JsonProperty("islem")
    private Integer operation = 1;

    @JsonProperty("fonTurKod")
    private String fundTypeCode = null;

    @JsonProperty("fonGrubu")
    private String fundGroup = null;

    @JsonProperty("donemGetiri1a")
    private String period1m = "1";

    @JsonProperty("donemGetiri3a")
    private String period3m = "1";

    @JsonProperty("donemGetiri6a")
    private String period6m = "1";

    @JsonProperty("donemGetiri1y")
    private String period1y = "1";

    @JsonProperty("donemGetiriyb")
    private String periodYtd = "0";

    @JsonProperty("donemGetiri3y")
    private String period3y = "1";

    @JsonProperty("donemGetiri5y")
    private String period5y = "1";

    @JsonProperty("basTarih")
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate startDate = null;

    @JsonProperty("bitTarih")
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate endDate = null;

    // 1 -> returns between basTarih and bitTarih
    // 2 -> returns at fixed timepoints (1/3/6 months, 1/3/5 years)
    @JsonProperty("calismaTipi")
    private Integer workingType = 2;

    @JsonProperty("getiriOrani")
    private String returnRatio = "1";

    public TefasFundReturnsRequest() {
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFundType() {
        return fundType;
    }

    public void setFundType(String fundType) {
        this.fundType = fundType;
    }

    public Integer getWorkingType() {
        return workingType;
    }

    public void setWorkingType(Integer workingType) {
        this.workingType = workingType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
