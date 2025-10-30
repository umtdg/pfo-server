package com.umtdg.pfo.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FundToBuy {
    @JsonProperty
    private String code;

    @JsonProperty
    private String title;

    @JsonProperty
    private float price;

    @JsonProperty
    private int amount;

    @JsonProperty
    private float weight;

    public FundToBuy() {
    }

    public FundToBuy(String code, String title, float price, int amount, float weight) {
        this.code = code;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.weight = weight;
    }
}
