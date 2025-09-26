package com.umtdg.pfo.portfolio.price;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class PortfolioFundPriceId {
    private UUID portfolioId;

    private String code;

    private LocalDate date;

    public PortfolioFundPriceId() {
    }

    public PortfolioFundPriceId(String code, LocalDate date, UUID portfolioId) {
        this.code = code;
        this.date = date;
        this.portfolioId = portfolioId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(UUID portfolioId) {
        this.portfolioId = portfolioId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof PortfolioFundPriceId))
            return false;

        PortfolioFundPriceId other = (PortfolioFundPriceId) obj;
        return Objects.equals(portfolioId, other.portfolioId)
            && Objects.equals(code, other.code)
            && Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portfolioId, code, date);
    }
}
