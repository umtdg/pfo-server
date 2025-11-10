package com.umtdg.pfo.portfolio.price;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TestPortfolioFundPrice {
    UUID getPortfolioId(PortfolioFundPrice fundPrice) {
        return (UUID) ReflectionTestUtils.getField(fundPrice, "portfolioId");
    }

    void setPortfolioId(PortfolioFundPrice fundPrice, UUID portfolioId) {
        ReflectionTestUtils.setField(fundPrice, "portfolioId", portfolioId);
    }

    @Test
    void shouldDefaultConstructPortfolioFundPrice() {
        PortfolioFundPrice fundPrice = new PortfolioFundPrice();

        assertNull(getPortfolioId(fundPrice));
        assertNull(fundPrice.getCode());
        assertNull(fundPrice.getDate());
        assertNull(fundPrice.getTitle());
        assertEquals(0.0f, fundPrice.getNormalizedWeight());
        assertEquals(0, fundPrice.getMinAmount());
        assertEquals(0.0f, fundPrice.getPrice());
    }

    @Test
    void givenEverything_shouldConstructPortfolioFundPrice() {
        String code = "FUN";
        LocalDate date = LocalDate.of(2025, 11, 10);
        String title = "Fund Title";
        float normWeight = 0.03f;
        int minAmount = 2;
        float price = 23.47f;

        PortfolioFundPrice fundPrice = new PortfolioFundPrice(
            code, title, normWeight, minAmount, price, date
        );

        assertNull(getPortfolioId(fundPrice));
        assertEquals(code, fundPrice.getCode());
        assertEquals(date, fundPrice.getDate());
        assertEquals(title, fundPrice.getTitle());
        assertEquals(normWeight, fundPrice.getNormalizedWeight());
        assertEquals(minAmount, fundPrice.getMinAmount());
        assertEquals(price, fundPrice.getPrice());
    }

    @Test
    void givenPortfolioFundPrice_shouldGetAndSetProperties() {
        String code = "FUN";
        UUID portfolioId = UUID.fromString("12345678-1234-5678-1234-567812345678");
        LocalDate date = LocalDate.of(2025, 11, 10);
        String title = "Fund Title";
        float normWeight = 0.03f;
        int minAmount = 2;
        float price = 23.47f;

        PortfolioFundPrice fundPrice = new PortfolioFundPrice();

        setPortfolioId(fundPrice, portfolioId);
        assertEquals(portfolioId, getPortfolioId(fundPrice));

        fundPrice.setCode(code);
        assertEquals(code, fundPrice.getCode());

        fundPrice.setDate(date);
        assertEquals(date, fundPrice.getDate());

        fundPrice.setTitle(title);
        assertEquals(title, fundPrice.getTitle());

        fundPrice.setNormalizedWeight(normWeight);
        assertEquals(normWeight, fundPrice.getNormalizedWeight());

        fundPrice.setMinAmount(minAmount);
        assertEquals(minAmount, fundPrice.getMinAmount());

        fundPrice.setPrice(price);
        assertEquals(price, fundPrice.getPrice());
    }
}
