package com.umtdg.pfo.fund.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TestFundStats {
    String getCode(FundStats stats) {
        return (String) ReflectionTestUtils.getField(stats, "code");
    }

    String getTitle(FundStats stats) {
        return (String) ReflectionTestUtils.getField(stats, "title");
    }

    double getPrice(FundStats stats) {
        return (double) ReflectionTestUtils.getField(stats, "price");
    }

    Double getReturnByIndex(FundStats stats, int index) {
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

        return (Double) ReflectionTestUtils.getField(stats, field);
    }

    @Test
    void givenCodeTitlePriceTotalValueAndReturns_shouldSetAndGetValues() {
        String code = "FUN";
        Double dailyReturn = 3.14;
        Double monthlyReturn = 7.26;
        Double threeMonthlyReturn = 20.15;
        Double sixMonthlyReturn = 43.29;
        Double yearlyReturn = 120.91;
        Double threeYearlyReturn = 0.0;
        Double fiveYearlyReturn = 0.0;
        String expectedString = "FundStats {code=FUN"
            + ", dailyReturn=3.14, monthlyReturn=7.26, threeMonthlyReturn=20.15"
            + ", sixMonthlyReturn=43.29, yearlyReturn=120.91, threeYearlyReturn=0.00"
            + ", fiveYearlyReturn=0.00}";

        FundStats stats = new FundStats();

        stats.setCode(code);
        assertEquals(code, getCode(stats));

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

        assertEquals(expectedString, stats.toString());
    }
}
