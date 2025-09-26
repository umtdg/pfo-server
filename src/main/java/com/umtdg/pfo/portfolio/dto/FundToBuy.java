package com.umtdg.pfo.portfolio.dto;

public class FundToBuy {
    private String code;
    private String title;
    private float price;
    private int amount;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
