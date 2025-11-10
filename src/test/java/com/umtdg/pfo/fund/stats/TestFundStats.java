package com.umtdg.pfo.fund.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TestFundStats {
    String getCode(FundStats stats) {
        return (String) ReflectionTestUtils.getField(stats, "code");
    }

    String getTitle(FundStats stats) {
        return (String) ReflectionTestUtils.getField(stats, "title");
    }

    float getLastPrice(FundStats stats) {
        return (float) ReflectionTestUtils.getField(stats, "lastPrice");
    }

    float getReturnByIndex(FundStats stats, int index) {
        String field = "dailyReturn";

        switch (index) {
            case 1 :
            default :
                field = "dailyReturn";
                break;
            case 2 :
                field = "monthlyReturn";
                break;
            case 3 :
                field = "threeMonthlyReturn";
                break;
            case 4 :
                field = "sixMonthlyReturn";
                break;
            case 5 :
                field = "yearlyReturn";
                break;
            case 6 :
                field = "threeYearlyReturn";
                break;
            case 7 :
                field = "fiveYearlyReturn";
                break;
        }

        return (float) ReflectionTestUtils.getField(stats, field);
    }

    @Test
    void givenCodeTitlePriceTotalValueAndReturns_shouldSetAndGetValues() {
        String code = "FUN";
        String title = "Fund Title";
        float lastPrice = 13.4f;
        float totalValue = 12345.90f;
        float dailyReturn = 3.14f;
        float monthlyReturn = 7.26f;
        float threeMonthlyReturn = 20.15f;
        float sixMonthlyReturn = 43.29f;
        float yearlyReturn = 120.91f;
        float threeYearlyReturn = 0.0f;
        float fiveYearlyReturn = 0.0f;
        LocalDate updatedAt = LocalDate.now().plusDays(1);
        String expectedString = "FundStats [code=FUN, lastPrice=13.40, totalValue=12345.90"
            + ", dailyReturn=3.14, monthlyReturn=7.26, threeMonthlyReturn=20.15"
            + ", sixMonthlyReturn=43.29, yearlyReturn=120.91, threeYearlyReturn=0.00"
            + ", fiveYearlyReturn=0.00]";

        FundStats stats = new FundStats();

        stats.setCode(code);
        assertEquals(code, getCode(stats));

        stats.setTitle(title);
        assertEquals(title, getTitle(stats));

        stats.setLastPrice(lastPrice);
        assertEquals(lastPrice, getLastPrice(stats));

        stats.setTotalValue(totalValue);
        assertEquals(totalValue, stats.getTotalValue());

        stats.setReturnByIndex(1, dailyReturn);
        assertEquals(dailyReturn, getReturnByIndex(stats, 1));

        stats.setReturnByIndex(2, monthlyReturn);
        assertEquals(monthlyReturn, getReturnByIndex(stats, 2));

        stats.setReturnByIndex(3, threeMonthlyReturn);
        assertEquals(threeMonthlyReturn, getReturnByIndex(stats, 3));

        stats.setReturnByIndex(4, sixMonthlyReturn);
        assertEquals(sixMonthlyReturn, getReturnByIndex(stats, 4));

        stats.setReturnByIndex(5, yearlyReturn);
        assertEquals(yearlyReturn, getReturnByIndex(stats, 5));

        stats.setReturnByIndex(6, threeYearlyReturn);
        assertEquals(threeYearlyReturn, getReturnByIndex(stats, 6));

        stats.setReturnByIndex(7, fiveYearlyReturn);
        assertEquals(fiveYearlyReturn, getReturnByIndex(stats, 7));

        assertEquals(LocalDate.now(), stats.getUpdatedAt());
        stats.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, stats.getUpdatedAt());

        assertEquals(expectedString, stats.toString());
    }
}
