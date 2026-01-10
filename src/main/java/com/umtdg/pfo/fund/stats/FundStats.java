package com.umtdg.pfo.fund.stats;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "fund_stats", indexes = {
        @Index(columnList = "six_monthly_return DESC"),
        @Index(columnList = "yearly_return DESC"),
        @Index(columnList = "three_yearly_return DESC"),
        @Index(columnList = "five_yearly_return DESC"),
    }
)
public class FundStats {
    public static final Set<String> ALLOWED_SORT_PROPERTIES = Set
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
    private String code;

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

    public void setCode(String code) {
        this.code = code;
    }

    public void setDailyReturn(double dailyReturn) {
        this.dailyReturn = dailyReturn;
    }

    public void setMonthlyReturn(double monthlyReturn) {
        this.monthlyReturn = monthlyReturn;
    }

    public void setThreeMonthlyReturn(double threeMonthlyReturn) {
        this.threeMonthlyReturn = threeMonthlyReturn;
    }

    public void setSixMonthlyReturn(double sixMonthlyReturn) {
        this.sixMonthlyReturn = sixMonthlyReturn;
    }

    public void setYearlyReturn(double yearlyReturn) {
        this.yearlyReturn = yearlyReturn;
    }

    public void setThreeYearlyReturn(double threeYearlyReturn) {
        this.threeYearlyReturn = threeYearlyReturn;
    }

    public void setFiveYearlyReturn(double fiveYearlyReturn) {
        this.fiveYearlyReturn = fiveYearlyReturn;
    }

    public void setReturnByIndex(Integer index, double value) {
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
        return String
            .format(
                "FundStats {code=%s, dailyReturn=%.2f"
                    + ", monthlyReturn=%.2f, threeMonthlyReturn=%.2f, sixMonthlyReturn=%.2f"
                    + ", yearlyReturn=%.2f, threeYearlyReturn=%.2f, fiveYearlyReturn=%.2f}",
                code,
                dailyReturn,
                monthlyReturn,
                threeMonthlyReturn,
                sixMonthlyReturn,
                yearlyReturn,
                threeYearlyReturn,
                fiveYearlyReturn
            );
    }
}
