package com.umtdg.pfo.portfolio.fund;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class TestPortfolioFund {
    @Test
    void givenCodeIdWeightAndMinAmount_shouldConstructPortfolioFund() {
        String fundCode = "FUN";
        UUID portfolioId = UUID.fromString("12345678-1234-5678-1234-567812345678");
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
        UUID portfolioId = UUID.fromString("12345678-1234-5678-1234-567812345678");
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
            "FUN", UUID.fromString("12345678-1234-5678-1234-567812345678"), 50.0f
        );

        portfolioFund.setFundCode("DUN");
        assertEquals("DUN", portfolioFund.getFundCode());

        portfolioFund
            .setPortfolioId(UUID.fromString("01234567-89AB-CDEF-0123-456789ABCDEF"));
        assertEquals(
            "01234567-89ab-cdef-0123-456789abcdef",
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
            "FUN", UUID.fromString("12345678-1234-5678-1234-567812345678"), 50.0f
        );

        String expectedString = "PortfolioFund{fundCode=FUN"
            + ", portfolioId=12345678-1234-5678-1234-567812345678, weight=50.00"
            + ", normWeight=0.00, minAmount=1}";
        assertEquals(expectedString, portfolioFund.toString());
    }
}
