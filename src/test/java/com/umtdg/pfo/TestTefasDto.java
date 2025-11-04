package com.umtdg.pfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.umtdg.pfo.fund.Fund;
import com.umtdg.pfo.fund.price.FundPrice;
import com.umtdg.pfo.tefas.TefasFund;

class TestTefasDto {
    @Test
    void givenTefasFund_thenConvertToFundAndFundPrice() {
        TefasFund tefasFund = new TefasFund();
        tefasFund.setCode("FUN");
        tefasFund.setDate(LocalDate.of(2025, 6, 27));
        tefasFund.setPrice(26.523359f);
        tefasFund.setNumShares(1328497.0f);
        tefasFund.setNumInvestors(778.0f);
        tefasFund.setMarketCap(35236202.48f);

        Fund fund = tefasFund.toFund();
        assertEquals(tefasFund.getCode(), fund.getCode());
        assertEquals(tefasFund.getTitle(), fund.getTitle());
        assertEquals("TEFAS", fund.getProvider());

        FundPrice fundPrice = tefasFund.toFundPrice();
        assertEquals(tefasFund.getCode(), fundPrice.getCode());
    }
}
