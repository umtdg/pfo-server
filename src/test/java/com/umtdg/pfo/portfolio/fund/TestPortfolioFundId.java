package com.umtdg.pfo.portfolio.fund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class TestPortfolioFundId {
    @Test
    void shouldDefaultConstructPortfolioFundId() {
        PortfolioFundId id = new PortfolioFundId();
        assertNull(id.getCode());
        assertNull(id.getPortfolioId());
    }

    @Test
    void givenFundCodeAndPortfolioId_shouldConstructPortfolioFundId() {
        String fundCode = "FUN";
        UUID portfolioId = UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8");

        PortfolioFundId id = new PortfolioFundId(fundCode, portfolioId);
        assertEquals(fundCode, id.getCode());
        assertEquals(portfolioId, id.getPortfolioId());
    }

    @Test
    void givenPortfolioFundId_shouldSetProperties() {
        PortfolioFundId id = new PortfolioFundId(
            "FUN", UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8")
        );

        id.setCode("DUN");
        assertEquals("DUN", id.getCode());

        id.setPortfolioId(UUID.fromString("6c354b15-60d0-43d2-9234-bfbafdaa1eb4"));
        assertEquals(
            "6c354b15-60d0-43d2-9234-bfbafdaa1eb4",
            id.getPortfolioId().toString()
        );
    }

    @Test
    void givenPortfolioFundIds_shouldCorrectlyCompareAndHash() {
        PortfolioFundId id1 = new PortfolioFundId(
            "FUN", UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8")
        );
        PortfolioFundId id2 = new PortfolioFundId(
            "DUN", UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8")
        );
        PortfolioFundId id3 = new PortfolioFundId(
            "FUN", UUID.fromString("6c354b15-60d0-43d2-9234-bfbafdaa1eb4")
        );
        PortfolioFundId id4 = new PortfolioFundId(
            "FUN", UUID.fromString("6c354b15-60d0-43d2-9234-bfbafdaa1eb4")
        );
        PortfolioFundId id5 = null;
        PortfolioFundId id6 = new PortfolioFundId();

        // object equality
        assertEquals(id3, id4);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id5);
        assertNotEquals(id1, id6);

        // hashes
        assertEquals(id1.hashCode(), id1.hashCode());
        assertEquals(id3.hashCode(), id4.hashCode());
        assertEquals(id6.hashCode(), id6.hashCode());

        assertNotEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
        assertNotEquals(id2.hashCode(), id3.hashCode());
        assertNotEquals(id1.hashCode(), id6.hashCode());
    }

    @Test
    void givenPortfolioFundId_shouldConvertToString() {
        String fundCode = "FUN";
        UUID portfolioId = UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8");

        PortfolioFundId id = new PortfolioFundId(fundCode, portfolioId);

        String expectedString = "PortfolioFundId{fundCode=FUN"
            + ", portfolioId=8a2392ac-7e47-4851-8698-f41a9d51d5e8}";
        assertEquals(expectedString, id.toString());
    }
}
