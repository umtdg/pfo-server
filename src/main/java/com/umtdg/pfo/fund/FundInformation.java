package com.umtdg.pfo.fund;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FundInformation
 */
public class FundInformation {
    private String code;
    private String title;
    private String provider;

    private LocalDate date;
    private float price;

    @JsonProperty("total_value")
    private float totalValue;

    public FundInformation() {
    }

    public FundInformation(
        String code, String title, String provider, LocalDate date, float price,
        float totalValue
    ) {
        this.code = code;
        this.title = title;
        this.provider = provider;
        this.date = date;
        this.price = price;
        this.totalValue = totalValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
    }
}
