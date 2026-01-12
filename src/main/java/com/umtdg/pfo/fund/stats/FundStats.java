package com.umtdg.pfo.fund.stats;

import java.util.Set;

import jakarta.persistence.Entity;
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
public class FundStats extends FundStatsBase {
    public static final Set<String> ALLOWED_SORT_PROPERTIES = Set
        .copyOf(FundStatsBase.BASE_SORT_PROPERTIES);

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
