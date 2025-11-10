package com.umtdg.pfo.fund.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TestFundInfo {
    String getProvider(FundInfo info) {
        return (String) ReflectionTestUtils.getField(info, "provider");
    }

    @Test
    void shouldDefaultConstructFundInfo() {
        FundInfo info = new FundInfo();

        assertNull(info.getCode());
        assertNull(info.getTitle());
        assertNull(getProvider(info));
        assertNull(info.getDate());
        assertEquals(0.0f, info.getPrice());
        assertEquals(0.0f, info.getTotalValue());
    }

    @Test
    void givenCodeTitleProviderDatePriceAndTotalValue_shouldConstructFundInfo() {
        String code = "FUN";
        String title = "Fund Title";
        String provider = null;
        LocalDate date = LocalDate.of(2025, 10, 13);
        float price = 65.07f;
        float totalValue = 12345678.90f;

        FundInfo info = new FundInfo(code, title, provider, date, price, totalValue);

        assertEquals(code, info.getCode());
        assertEquals(title, info.getTitle());
        assertNull(getProvider(info));
        assertEquals(date, info.getDate());
        assertEquals(price, info.getPrice());
        assertEquals(totalValue, info.getTotalValue());
    }
}
