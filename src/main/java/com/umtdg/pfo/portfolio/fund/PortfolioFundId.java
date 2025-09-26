package com.umtdg.pfo.portfolio.fund;

import java.util.Objects;
import java.util.UUID;

public class PortfolioFundId {
    private String fundCode;
    private UUID portfolioId;

    public PortfolioFundId() {
    }

    public PortfolioFundId(String fundCode, UUID portfolioId) {
        this.fundCode = fundCode;
        this.portfolioId = portfolioId;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
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

        if (!(obj instanceof PortfolioFundId))
            return false;

        PortfolioFundId other = (PortfolioFundId) obj;
        return Objects.equals(fundCode, other.fundCode)
            && Objects.equals(portfolioId, other.portfolioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fundCode, portfolioId);
    }

    @Override
    public String toString() {
        return String
            .format(
                "PortfolioFundId{fundCode=%s, portfolioId=%s}",
                fundCode,
                portfolioId
            );
    }
}
