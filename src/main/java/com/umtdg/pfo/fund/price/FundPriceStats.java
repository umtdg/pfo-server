package com.umtdg.pfo.fund.price;

import java.time.LocalDate;
import java.util.Set;

import org.hibernate.annotations.View;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fund_price_stats_view")
@View(query = """
    SELECT
        fp.code,
        fp.date,
        fp.price,
        fp.total_value,
        fs.daily_return,
        fs.monthly_return,
        fs.three_monthly_return,
        fs.six_monthly_return,
        fs.yearly_return,
        fs.three_yearly_return,
        fs.five_yearly_return
    FROM fund_price fp
    INNER JOIN (
        SELECT code, MAX(date) AS max_date
        FROM fund_price
        GROUP BY code
    ) fpm ON fp.code = fpm.code AND fp.date = fpm.max_date
    INNER JOIN fund_stats fs ON fp.code = fs.code
    """)
public class FundPriceStats {
    public static final Set<String> ALLOWED_SORT_PROPETIES = Set
        .of(
            "code",
            "date",
            "price",
            "totalValue",
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
    private String code;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price")
    private double price;

    @Column(name = "total_value")
    @JsonProperty("total_value")
    private double totalValue;

    @Column(name = "daily_return")
    @JsonProperty("daily_return")
    private Double dailyReturn;

    @Column(name = "monthly_return")
    @JsonProperty("monthly_return")
    private Double monthlyReturn;

    @Column(name = "three_monthly_return")
    @JsonProperty("three_monthly_return")
    private Double threeMonthlyReturn;

    @Column(name = "six_monthly_return")
    @JsonProperty("six_monthly_return")
    private Double sixMonthlyReturn;

    @Column(name = "yearly_return")
    @JsonProperty("yearly_return")
    private Double yearlyReturn;

    @Column(name = "three_yearly_return")
    @JsonProperty("three_yearly_return")
    private Double threeYearlyReturn;

    @Column(name = "five_yearly_return")
    @JsonProperty("five_yearly_return")
    private Double fiveYearlyReturn;

    public FundPriceStats() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
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
