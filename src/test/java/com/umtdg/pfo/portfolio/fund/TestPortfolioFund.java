package com.umtdg.pfo.portfolio.fund;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class TestPortfolioFund {
    @Test
    void givenCodeAndId_shouldConstructPortfolioFund() {
        String code = "FUN";
        UUID portfolioId = UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8");

        PortfolioFund portfolioFund = new PortfolioFund(code, portfolioId);

        assertEquals(code, portfolioFund.getCode());
        assertEquals(portfolioId, portfolioFund.getPortfolioId());
        assertEquals(50.0, portfolioFund.getWeight());
        assertEquals(1, portfolioFund.getMinAmount());
        assertEquals(0.0, portfolioFund.getNormWeight());
    }

    @Test
    void givenPortfolioFund_shouldSetProperties() {
        PortfolioFund portfolioFund = new PortfolioFund(
            "FUN", UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8")
        );

        portfolioFund.setCode("DUN");
        assertEquals("DUN", portfolioFund.getCode());

        portfolioFund
            .setPortfolioId(UUID.fromString("6c354b15-60d0-43d2-9234-bfbafdaa1eb4"));
        assertEquals(
            "6c354b15-60d0-43d2-9234-bfbafdaa1eb4",
            portfolioFund.getPortfolioId().toString()
        );

        portfolioFund.setWeight(75.0);
        assertEquals(75.0, portfolioFund.getWeight());

        portfolioFund.setMinAmount(3);
        assertEquals(3, portfolioFund.getMinAmount());

        portfolioFund.setNormWeight(0.027);
        assertEquals(0.027, portfolioFund.getNormWeight());
    }

    @Test
    void givenPortfolioFund_shouldConvertToString() {
        PortfolioFund portfolioFund = new PortfolioFund(
            "FUN", UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8")
        );

        String expectedString = "PortfolioFund{fundCode=FUN"
            + ", portfolioId=8a2392ac-7e47-4851-8698-f41a9d51d5e8, weight=50.00"
            + ", normWeight=0.00, minAmount=1}";
        assertEquals(expectedString, portfolioFund.toString());
    }
}
