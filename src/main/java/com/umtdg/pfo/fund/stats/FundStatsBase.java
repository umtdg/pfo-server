package com.umtdg.pfo.fund.stats;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class FundStatsBase {
    public static final Set<String> BASE_SORT_PROPERTIES = Set
        .of(
            "code",
            "dailyReturn",
            "monthlyReturn",
            "threeMonthlyReturn",
            "sixMonthlyReturn",
            "yearlyReturn",
            "threeYearlyReturn",
            "fiveYearlyReturn"
        );

    @Id
    @Column(name = "code", length = 3)
    @JsonProperty
    protected String code;

    @Column(name = "daily_return")
    @JsonProperty("daily_return")
    protected Double dailyReturn;

    @Column(name = "monthly_return")
    @JsonProperty("monthly_return")
    protected Double monthlyReturn;

    @Column(name = "three_monthly_return")
    @JsonProperty("three_monthly_return")
    protected Double threeMonthlyReturn;

    @Column(name = "six_monthly_return")
    @JsonProperty("six_monthly_return")
    protected Double sixMonthlyReturn;

    @Column(name = "yearly_return")
    @JsonProperty("yearly_return")
    protected Double yearlyReturn;

    @Column(name = "three_yearly_return")
    @JsonProperty("three_yearly_return")
    protected Double threeYearlyReturn;

    @Column(name = "five_yearly_return")
    @JsonProperty("five_yearly_return")
    protected Double fiveYearlyReturn;

    public void setCode(String code) {
        this.code = code;
    }

    public static Set<String> getBaseSortProperties() {
        return BASE_SORT_PROPERTIES;
    }

    public Double getDailyReturn() {
        return dailyReturn;
    }

    public void setDailyReturn(Double dailyReturn) {
        this.dailyReturn = dailyReturn;
    }

    public Double getMonthlyReturn() {
        return monthlyReturn;
    }

    public void setMonthlyReturn(Double monthlyReturn) {
        this.monthlyReturn = monthlyReturn;
    }

    public Double getThreeMonthlyReturn() {
        return threeMonthlyReturn;
    }

    public void setThreeMonthlyReturn(Double threeMonthlyReturn) {
        this.threeMonthlyReturn = threeMonthlyReturn;
    }

    public Double getSixMonthlyReturn() {
        return sixMonthlyReturn;
    }

    public void setSixMonthlyReturn(Double sixMonthlyReturn) {
        this.sixMonthlyReturn = sixMonthlyReturn;
    }

    public Double getYearlyReturn() {
        return yearlyReturn;
    }

    public void setYearlyReturn(Double yearlyReturn) {
        this.yearlyReturn = yearlyReturn;
    }

    public Double getThreeYearlyReturn() {
        return threeYearlyReturn;
    }

    public void setThreeYearlyReturn(Double threeYearlyReturn) {
        this.threeYearlyReturn = threeYearlyReturn;
    }

    public Double getFiveYearlyReturn() {
        return fiveYearlyReturn;
    }

    public void setFiveYearlyReturn(Double fiveYearlyReturn) {
        this.fiveYearlyReturn = fiveYearlyReturn;
    }
}
