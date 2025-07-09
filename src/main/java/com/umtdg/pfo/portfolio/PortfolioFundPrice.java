package com.umtdg.pfo.portfolio;

public class PortfolioFundPrice {
    public String code;
    public String title;
    public float normWeight;
    public int minAmount;
    public float price;

    public PortfolioFundPrice() {
    }

    public PortfolioFundPrice(String code, String title, float normWeight, int minAmount, float price) {
        this.code = code;
        this.title = title;
        this.normWeight = normWeight;
        this.minAmount = minAmount;
        this.price = price;
    }
}
