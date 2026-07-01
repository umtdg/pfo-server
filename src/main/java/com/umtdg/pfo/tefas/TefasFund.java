package com.umtdg.pfo.tefas;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.umtdg.pfo.fund.Fund;
import com.umtdg.pfo.fund.price.FundPrice;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TefasFund {
    @JsonProperty("fonKodu")
    private String code;

    @JsonProperty("fonUnvan")
    private String title;

    @JsonProperty("tarih")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("fiyat")
    private Double price;

    @JsonProperty("portfoyBuyukluk")
    private Double marketCap = 0.0;

    @JsonProperty("tedPaySayisi")
    private Integer numShares;

    @JsonProperty("kisiSayisi")
    private Integer numInvestors;

    public Fund toFund() {
        return new Fund(code, title, "TEFAS");
    }

    public FundPrice toFundPrice() {
        return new FundPrice(code, date, price, marketCap);
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }

    public Integer getNumShares() {
        return numShares;
    }

    public void setNumShares(Integer numShares) {
        this.numShares = numShares;
    }

    public Integer getNumInvestors() {
        return numInvestors;
    }

    public void setNumInvestors(Integer numInvestors) {
        this.numInvestors = numInvestors;
    }
}
