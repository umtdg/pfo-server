package com.umtdg.pfo.fund.price;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TestFundPrice {
    @Test
    void givenCodeDatePriceAndTotalValue_shouldConstructFundInfo() {
        String code = "FUN";
        LocalDate date = LocalDate.of(2025, 10, 13);
        double price = 65.07f;
        double totalValue = 12345678.90f;

        FundPrice info = new FundPrice(code, date, price, totalValue);

        assertEquals(code, info.getCode());
        assertEquals(date, info.getDate());
        assertEquals(price, info.getPrice());
        assertEquals(totalValue, info.getTotalValue());
    }
}
