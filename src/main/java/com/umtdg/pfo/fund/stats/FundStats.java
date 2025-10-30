package com.umtdg.pfo.fund.stats;

import java.time.LocalDate;

import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "fund_stats", indexes = {
        @Index(columnList = "total_value DESC"),
        @Index(columnList = "six_monthly_return DESC"),
        @Index(columnList = "yearly_return DESC"),
        @Index(columnList = "three_yearly_return DESC"),
        @Index(columnList = "five_yearly_return DESC"),
    }
)
public class FundStats {
    @Id
    @Column(name = "code", length = 3)
    @JsonProperty
    private String code;

    @Column(name = "title", nullable = false)
    @JsonProperty
    private String title;

    @Column(name = "last_price", nullable = false)
    @JsonProperty("last_price")
    private float lastPrice = 0.0f;

    @Column(name = "total_value", nullable = false)
    @JsonProperty("total_value")
    private float totalValue = 0.0f;

    @Column(name = "daily_return", nullable = false)
    @JsonProperty("daily_return")
    private float dailyReturn = 0.0f;

    @Column(name = "monthly_return", nullable = false)
    @JsonProperty("monthly_return")
    private float monthlyReturn = 0.0f;

    @Column(name = "three_monthly_return", nullable = false)
    @JsonProperty("three_monthly_return")
    private float threeMonthlyReturn = 0.0f;

    @Column(name = "six_monthly_return", nullable = false)
    @JsonProperty("six_monthly_return")
    private float sixMonthlyReturn = 0.0f;

    @Column(name = "yearly_return", nullable = false)
    @JsonProperty("yearly_return")
    private float yearlyReturn = 0.0f;

    @Column(name = "three_yearly_return", nullable = false)
    @JsonProperty("three_yearly_return")
    private float threeYearlyReturn = 0.0f;

    @Column(name = "five_yearly_return", nullable = false)
    @JsonProperty("five_yearly_return")
    private float fiveYearlyReturn = 0.0f;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @JsonProperty("updated_at")
    private LocalDate updatedAt = LocalDate.now();

    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setDailyReturn(float dailyReturn) {
        this.dailyReturn = dailyReturn;
    }

    public void setMonthlyReturn(float monthlyReturn) {
        this.monthlyReturn = monthlyReturn;
    }

    public void setThreeMonthlyReturn(float threeMonthlyReturn) {
        this.threeMonthlyReturn = threeMonthlyReturn;
    }

    public void setSixMonthlyReturn(float sixMonthlyReturn) {
        this.sixMonthlyReturn = sixMonthlyReturn;
    }

    public void setYearlyReturn(float yearlyReturn) {
        this.yearlyReturn = yearlyReturn;
    }

    public void setThreeYearlyReturn(float threeYearlyReturn) {
        this.threeYearlyReturn = threeYearlyReturn;
    }

    public void setFiveYearlyReturn(float fiveYearlyReturn) {
        this.fiveYearlyReturn = fiveYearlyReturn;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setReturnByIndex(Integer index, float value) {
        if (index == null) return;

        if (index == 1) {
            setDailyReturn(value);
        } else if (index == 2) {
            setMonthlyReturn(value);
        } else if (index == 3) {
            setThreeMonthlyReturn(value);
        } else if (index == 4) {
            setSixMonthlyReturn(value);
        } else if (index == 5) {
            setYearlyReturn(value);
        } else if (index == 6) {
            setThreeYearlyReturn(value);
        } else if (index == 7) {
            setFiveYearlyReturn(value);
        }
    }

    @Override
    public String toString() {
        return "FundStats [code=" + code + ", lastPrice=" + lastPrice + ", totalValue="
            + totalValue + ", dailyReturn=" + dailyReturn + ", monthlyReturn="
            + monthlyReturn + ", threeMonthlyReturn=" + threeMonthlyReturn
            + ", sixMonthlyReturn=" + sixMonthlyReturn + ", yearlyReturn="
            + yearlyReturn + ", threeYearlyReturn=" + threeYearlyReturn
            + ", fiveYearlyReturn=" + fiveYearlyReturn + "]";
    }
}
