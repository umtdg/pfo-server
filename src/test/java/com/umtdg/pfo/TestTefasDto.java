package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.umtdg.pfo.fund.Fund;
import com.umtdg.pfo.fund.price.FundPrice;
import com.umtdg.pfo.fund.stats.FundStats;
import com.umtdg.pfo.tefas.TefasFund;
import com.umtdg.pfo.tefas.TefasFundReturns;

class TestTefasDto {
    @Test
    void givenTefasFund_thenConvertToFundAndFundPrice() {
        TefasFund tefasFund = new TefasFund();
        tefasFund.setCode("FUN");
        tefasFund.setDate(LocalDate.of(2025, 6, 27));
        tefasFund.setPrice(26.523359);
        tefasFund.setNumShares(1328497L);
        tefasFund.setNumInvestors(778L);
        tefasFund.setMarketCap(35236202.48);

        Fund fund = tefasFund.toFund();
        assertEquals(tefasFund.getCode(), fund.getCode());
        assertEquals(tefasFund.getTitle(), fund.getTitle());
        assertEquals("TEFAS", fund.getProvider());

        FundPrice fundPrice = tefasFund.toFundPrice();
        assertEquals(tefasFund.getCode(), fundPrice.getCode());
    }

    @Test
    void givenTefasFundReturns_thenConvertToFundStatsAsRatios() {
        TefasFundReturns returns = new TefasFundReturns();
        returns.setCode("AJK");
        returns.setReturn1m(2.451);
        returns.setReturn3m(6.099);
        returns.setReturn6m(10.5705);
        returns.setReturn1y(21.8585);
        returns.setReturn3y(88.9042);
        returns.setReturn5y(489.1516);

        FundStats stats = returns.toFundStats();

        assertEquals("AJK", stats.getCode());
        // Percentages are stored as ratios (percentage / 100).
        assertEquals(0.02451, stats.getMonthlyReturn(), 1e-9);
        assertEquals(0.06099, stats.getThreeMonthlyReturn(), 1e-9);
        assertEquals(0.105705, stats.getSixMonthlyReturn(), 1e-9);
        assertEquals(0.218585, stats.getYearlyReturn(), 1e-9);
        assertEquals(0.889042, stats.getThreeYearlyReturn(), 1e-9);
        assertEquals(4.891516, stats.getFiveYearlyReturn(), 1e-9);
        // The API does not provide a daily return.
        assertNull(stats.getDailyReturn());
    }

    @Test
    void givenNullReturns_thenFundStatsFieldsAreNull() {
        TefasFundReturns returns = new TefasFundReturns();
        returns.setCode("AU1");
        returns.setReturn1m(-9.4002);
        // All longer-period returns are null.

        FundStats stats = returns.toFundStats();

        assertEquals("AU1", stats.getCode());
        assertEquals(-0.094002, stats.getMonthlyReturn(), 1e-9);
        assertNull(stats.getThreeMonthlyReturn());
        assertNull(stats.getSixMonthlyReturn());
        assertNull(stats.getYearlyReturn());
        assertNull(stats.getThreeYearlyReturn());
        assertNull(stats.getFiveYearlyReturn());
        assertNull(stats.getDailyReturn());
    }
}
