package com.umtdg.pfo.portfolio.fund;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class TestPortfolioFund {
    @Test
    void givenCodeIdWeightAndMinAmount_shouldConstructPortfolioFund() {
        String fundCode = "FUN";
        UUID portfolioId = UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8");
        float weight = 0.3f;

        PortfolioFund portfolioFund = new PortfolioFund(fundCode, portfolioId, weight);

        assertEquals(fundCode, portfolioFund.getFundCode());
        assertEquals(portfolioId, portfolioFund.getPortfolioId());
        assertEquals(weight, portfolioFund.getWeight());
        assertEquals(1, portfolioFund.getMinAmount());
        assertEquals(0.0f, portfolioFund.getNormWeight());
    }

    @Test
    void givenCodeIdAndWeight_shouldConstructPortfolioFund() {
        String fundCode = "FUN";
        UUID portfolioId = UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8");
        float weight = 25.0f;
        int minAmount = 5;

        PortfolioFund portfolioFund = new PortfolioFund(
            fundCode, portfolioId, weight, minAmount
        );

        assertEquals(fundCode, portfolioFund.getFundCode());
        assertEquals(portfolioId, portfolioFund.getPortfolioId());
        assertEquals(weight, portfolioFund.getWeight());
        assertEquals(minAmount, portfolioFund.getMinAmount());
        assertEquals(0.0f, portfolioFund.getNormWeight());
    }

    @Test
    void givenPortfolioFund_shouldSetProperties() {
        PortfolioFund portfolioFund = new PortfolioFund(
            "FUN", UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8"), 50.0f
        );

        portfolioFund.setFundCode("DUN");
        assertEquals("DUN", portfolioFund.getFundCode());

        portfolioFund
            .setPortfolioId(UUID.fromString("6c354b15-60d0-43d2-9234-bfbafdaa1eb4"));
        assertEquals(
            "6c354b15-60d0-43d2-9234-bfbafdaa1eb4",
            portfolioFund.getPortfolioId().toString()
        );

        portfolioFund.setWeight(75.0f);
        assertEquals(75.0f, portfolioFund.getWeight());

        portfolioFund.setMinAmount(3);
        assertEquals(3, portfolioFund.getMinAmount());

        portfolioFund.setNormWeight(0.027f);
        assertEquals(0.027f, portfolioFund.getNormWeight());
    }

    @Test
    void givenPortfolioFund_shouldConvertToString() {
        PortfolioFund portfolioFund = new PortfolioFund(
            "FUN", UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8"), 50.0f
        );

        String expectedString = "PortfolioFund{fundCode=FUN"
            + ", portfolioId=8a2392ac-7e47-4851-8698-f41a9d51d5e8, weight=50.00"
            + ", normWeight=0.00, minAmount=1}";
        assertEquals(expectedString, portfolioFund.toString());
    }
}
