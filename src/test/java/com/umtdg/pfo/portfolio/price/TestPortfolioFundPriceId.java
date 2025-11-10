package com.umtdg.pfo.portfolio.price;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class TestPortfolioFundPriceId {
    @Test
    void shouldDefaultConstructFundPriceId() {
        PortfolioFundPriceId id = new PortfolioFundPriceId();

        assertNull(id.getCode());
        assertNull(id.getDate());
        assertNull(id.getPortfolioId());
    }

    @Test
    void givenCodeDateAndPortfolioId_shouldConstructPortfolioFundPriceId() {
        String code = "FUN";
        LocalDate date = LocalDate.of(2025, 03, 14);
        UUID portfolioId = UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8");

        PortfolioFundPriceId id = new PortfolioFundPriceId(code, date, portfolioId);

        assertEquals(code, id.getCode());
        assertEquals(date, id.getDate());
        assertEquals(portfolioId, id.getPortfolioId());
    }

    @Test
    void givenPortfolioFundPriceId_shouldGetAndSetProperties() {
        String code = "FUN";
        LocalDate date = LocalDate.of(2025, 03, 14);
        UUID portfolioId = UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8");

        PortfolioFundPriceId id = new PortfolioFundPriceId();

        id.setCode(code);
        assertEquals(code, id.getCode());

        id.setDate(date);
        assertEquals(date, id.getDate());

        id.setPortfolioId(portfolioId);
        assertEquals(portfolioId, id.getPortfolioId());
    }

    @Test
    void givenPortfolioFundPriceIds_shouldCorrectlyCompareAndHash() {
        PortfolioFundPriceId id1 = new PortfolioFundPriceId(
            "FUN", LocalDate.of(2025, 12, 07),
            UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8")
        );
        PortfolioFundPriceId id2 = new PortfolioFundPriceId(
            "DUN", LocalDate.of(2025, 12, 07),
            UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8")
        );
        PortfolioFundPriceId id3 = new PortfolioFundPriceId(
            "FUN", LocalDate.of(2024, 12, 07),
            UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8")
        );
        PortfolioFundPriceId id4 = new PortfolioFundPriceId(
            "FUN", LocalDate.of(2025, 12, 07),
            UUID.fromString("6c354b15-60d0-43d2-9234-bfbafdaa1eb4")
        );
        PortfolioFundPriceId id5 = new PortfolioFundPriceId(
            "FUN", LocalDate.of(2025, 12, 07),
            UUID.fromString("6c354b15-60d0-43d2-9234-bfbafdaa1eb4")
        );
        PortfolioFundPriceId id6 = new PortfolioFundPriceId();
        PortfolioFundPriceId id7 = null;

        // object equality
        assertEquals(id4, id5);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertNotEquals(id1, id4);
        assertNotEquals(id1, id6);
        assertNotEquals(id1, id7);

        // hashes
        assertEquals(id4.hashCode(), id5.hashCode());

        assertNotEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
        assertNotEquals(id1.hashCode(), id4.hashCode());
        assertNotEquals(id1.hashCode(), id6.hashCode());
    }
}
