package com.umtdg.pfo.fund;

import java.time.LocalDate;

import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fund_stats")
public class FundStats {
    @Id
    @Column(name = "code", length = 3)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "last_price", nullable = false)
    @JsonProperty("last_price")
    private float lastPrice;

    @Column(name = "total_value", nullable = false)
    @JsonProperty("total_value")
    private float totalValue;

    @Column(name = "daily_return", nullable = true)
    @JsonProperty("daily_return")
    private Float dailyReturn = null;

    @Column(name = "monthly_return", nullable = true)
    @JsonProperty("monthly_return")
    private Float monthlyReturn = null;

    @Column(name = "three_monthly_return", nullable = true)
    @JsonProperty("three_monthly_return")
    private Float threeMonthlyReturn = null;

    @Column(name = "six_monthly_return", nullable = true)
    @JsonProperty("six_monthly_return")
    private Float sixMonthlyReturn = null;

    @Column(name = "yearly_return", nullable = true)
    @JsonProperty("yearly_return")
    private Float yearlyReturn = null;

    @Column(name = "three_yearly_return", nullable = true)
    @JsonProperty("three_yearly_return")
    private Float threeYearlyReturn = null;

    @Column(name = "five_yearly_return", nullable = true)
    @JsonProperty("five_yearly_return")
    private Float fiveYearlyReturn = null;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    public FundStats() {
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

    public float getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(float totalValue) {
        this.totalValue = totalValue;
    }

    public Float getDailyReturn() {
        return dailyReturn;
    }

    public void setDailyReturn(Float dailyReturn) {
        this.dailyReturn = dailyReturn;
    }

    public Float getMonthlyReturn() {
        return monthlyReturn;
    }

    public void setMonthlyReturn(Float monthlyReturn) {
        this.monthlyReturn = monthlyReturn;
    }

    public Float getThreeMonthlyReturn() {
        return threeMonthlyReturn;
    }

    public void setThreeMonthlyReturn(Float threeMonthlyReturn) {
        this.threeMonthlyReturn = threeMonthlyReturn;
    }

    public Float getSixMonthlyReturn() {
        return sixMonthlyReturn;
    }

    public void setSixMonthlyReturn(Float sixMonthlyReturn) {
        this.sixMonthlyReturn = sixMonthlyReturn;
    }

    public Float getYearlyReturn() {
        return yearlyReturn;
    }

    public void setYearlyReturn(Float yearlyReturn) {
        this.yearlyReturn = yearlyReturn;
    }

    public Float getThreeYearlyReturn() {
        return threeYearlyReturn;
    }

    public void setThreeYearlyReturn(Float threeYearlyReturn) {
        this.threeYearlyReturn = threeYearlyReturn;
    }

    public Float getFiveYearlyReturn() {
        return fiveYearlyReturn;
    }

    public void setFiveYearlyReturn(Float fiveYearlyReturn) {
        this.fiveYearlyReturn = fiveYearlyReturn;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

}
