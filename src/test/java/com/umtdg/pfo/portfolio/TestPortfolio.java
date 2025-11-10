package com.umtdg.pfo.portfolio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class TestPortfolio {
    @Test
    void shouldDefaultConstructPortfolioAndGetSetProperties() {
        UUID uuid = UUID.fromString("8a2392ac-7e47-4851-8698-f41a9d51d5e8");
        String name = "Test Portfolio";

        Portfolio portfolio = new Portfolio();

        assertNull(portfolio.getId());
        assertNull(portfolio.getName());

        portfolio.setId(uuid);
        assertEquals(uuid, portfolio.getId());

        portfolio.setName(name);
        assertEquals(name, portfolio.getName());
    }
}
